package com.didichuxing.doraemonkit.kit.network.rpc;


import android.text.TextUtils;

import com.didichuxing.doraemonkit.kit.network.NetworkManager;
import com.didichuxing.doraemonkit.kit.network.room_db.DokitDbManager;
import com.didichuxing.doraemonkit.kit.network.room_db.MockInterceptApiBean;
import com.didichuxing.doraemonkit.kit.network.room_db.MockTemplateApiBean;
import com.didichuxing.doraemonkit.kit.network.stream.InputStreamProxy;
import com.didichuxing.foundation.net.MimeType;
import com.didichuxing.foundation.net.http.HttpEntity;
import com.didichuxing.foundation.net.http.HttpMethod;
import com.didichuxing.foundation.net.rpc.http.HttpRpcRequest;
import com.didichuxing.foundation.net.rpc.http.HttpRpcResponse;
import com.didichuxing.foundation.rpc.RpcInterceptor;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.nio.charset.Charset;

import didihttp.HttpUrl;


/**
 * @author: linjizong
 * 2019/3/6
 * @desc: mock请求拦截器
 */
public class RpcMockInterceptor implements RpcInterceptor<HttpRpcRequest, HttpRpcResponse> {
    private static final String TAG = "RpcMockInterceptor";

    @Override
    public HttpRpcResponse intercept(RpcChain<HttpRpcRequest, HttpRpcResponse> chain) throws IOException {
        HttpRpcRequest oldRequest = chain.getRequest();

        HttpRpcResponse oldResponse = chain.proceed(oldRequest);

        HttpUrl url = HttpUrl.parse(oldRequest.getUrl());
        String host = url.host();
        //如果是mock平台的接口则不进行拦截
        if (host.equalsIgnoreCase(NetworkManager.MOCK_HOST)) {
            return oldResponse;
        }
        //path  /test/upload/img
        String path = URLDecoder.decode(url.encodedPath(), "utf-8");
        //兼容滴滴内部外网映射环境  该环境的 path上会多一级/kop_xxx/路径
        String queries = url.query();
        String strRequestBody = transformRequestBody(oldRequest.getEntity());
        String interceptMatchedId = DokitDbManager.getInstance().isMockMatched(path, queries, strRequestBody, DokitDbManager.MOCK_API_INTERCEPT, DokitDbManager.FROM_SDK_DIDI);
        String templateMatchedId = DokitDbManager.getInstance().isMockMatched(path, queries, strRequestBody, DokitDbManager.MOCK_API_TEMPLATE, DokitDbManager.FROM_SDK_DIDI);

        try {
            //是否命中拦截规则
            if (!TextUtils.isEmpty(interceptMatchedId)) {
                return matchedInterceptRule(url, path, interceptMatchedId, templateMatchedId, oldRequest, oldResponse, chain);
            }

            //是否命中模板规则
            oldResponse = matchedTemplateRule(oldResponse, path, templateMatchedId);

        } catch (Exception e) {
            e.printStackTrace();
            return oldResponse;
        }
        return oldResponse;
    }


    /**
     * 将request body 转化成json字符串
     *
     * @return
     */
    private String transformRequestBody(HttpEntity requestBody) {
        if (requestBody == null) {
            return "";
        }
        return "";
    }


    /**
     * 命中拦截规则
     * 返回新的response
     *
     * @param interceptMatchedId
     * @return
     */
    private HttpRpcResponse matchedInterceptRule(HttpUrl url, String path, String interceptMatchedId, String templateMatchedId, HttpRpcRequest oldRequest, HttpRpcResponse oldResponse, RpcChain<HttpRpcRequest, HttpRpcResponse> chain) throws Exception {
        //判断是否需要重定向数据接口
        //http https
        String scheme = url.scheme();
        MockInterceptApiBean interceptApiBean = (MockInterceptApiBean) DokitDbManager.getInstance().getInterceptApiByIdInMap(path, interceptMatchedId, DokitDbManager.FROM_SDK_DIDI);
        if (interceptApiBean == null) {
            return matchedTemplateRule(oldResponse, path, templateMatchedId);
        }

        String selectedSceneId = interceptApiBean.getSelectedSceneId();
        //开关是否被打开
        if (!interceptApiBean.isOpen()) {
            return matchedTemplateRule(oldResponse, path, templateMatchedId);
        }

        //判断是否有选中的场景
        if (TextUtils.isEmpty(selectedSceneId)) {
            return matchedTemplateRule(oldResponse, path, templateMatchedId);
        }

        StringBuffer sb = new StringBuffer();
        String newUrl;
        if (NetworkManager.MOCK_SCHEME_HTTP.contains(scheme.toLowerCase())) {
            newUrl = sb.append(NetworkManager.MOCK_SCHEME_HTTP).append(NetworkManager.MOCK_HOST).append("/api/app/scene/").append(selectedSceneId).toString();
        } else {
            newUrl = sb.append(NetworkManager.MOCK_SCHEME_HTTPS).append(NetworkManager.MOCK_HOST).append("/api/app/scene/").append(selectedSceneId).toString();
        }

        //LogHelper.i("MOCK_INTERCEPT", "name===>" + interceptApiBean.getMockApiName() + "  newUrl=====>" + newUrl);

        HttpRpcRequest mockRequest = new HttpRpcRequest.Builder()
                .setMethod(HttpMethod.GET, null)
                .setUrl(newUrl).build();
        HttpRpcResponse mockResponse = chain.proceed(mockRequest);
        if (mockResponse.isSuccessful()) {
            //判断新的response是否有数据
            if (newResponseHasData(mockResponse)) {
                return matchedTemplateRule(mockResponse, path, templateMatchedId);
            } else {
                return matchedTemplateRule(oldResponse, path, templateMatchedId);
            }
        }
        return matchedTemplateRule(oldResponse, path, templateMatchedId);

    }

    /**
     * 命中模板规则
     * 保存正常接口的response到数据库
     *
     * @return
     */
    private HttpRpcResponse matchedTemplateRule(HttpRpcResponse response, String path, String templateMatchedId) throws Exception {
        //命中模板规则
        if (TextUtils.isEmpty(templateMatchedId)) {
            return response;
        }
        MockTemplateApiBean templateApiBean = (MockTemplateApiBean) DokitDbManager.getInstance().getTemplateApiByIdInMap(path, templateMatchedId, DokitDbManager.FROM_SDK_DIDI);
        if (templateApiBean == null) {
            return response;
        }
        //LogHelper.i("MOCK_TEMPLATE", "name=====>" + templateApiBean.getMockApiName() + "   isOpen===>" + templateApiBean.isOpen());
        if (templateApiBean.isOpen()) {
            //保存老的response 数据到数据库
            response = saveResponse2DB(response, templateApiBean);
        }

        return response;
    }


    /**
     * 保存匹配中的数据到本地数据库
     * 因为中间读取过inputStream 所以需要重新设置response 的body
     *
     * @param response
     * @param mockApi
     * @throws Exception
     */
    private HttpRpcResponse saveResponse2DB(HttpRpcResponse response, MockTemplateApiBean mockApi) throws Exception {
        if (!response.isSuccessful()) {
            return response;
        }
        String host = HttpUrl.parse(response.getRequest().getUrl()).host();
        //LogHelper.i(TAG, "host====>" + host);
        //这里不能直接使用response.body().string()的方式输出日志
        //因为response.body().string()之后，response中的流会被关闭，程序会报错，我们需要创建出一
        //个新的response给应用层处理

        if (response.getEntity() == null || response.getEntity().getContent() == null) {
            return response;
        }
        HttpEntity entity = response.getEntity();
        InputStream responseStream = entity.getContent();

        //新建InputStream 代理 并设置到新的response中去
        InputStream newInputStream = new InputStreamProxy(
                responseStream,
                new MockResponseHandler(host, mockApi));
        // 必须重置response的body
        return resetResponseInputStream(response, entity, newInputStream);

    }


    /**
     * 对response 的Entity进行重置
     *
     * @param response
     * @param entity
     * @param newInputStream
     */
    private HttpRpcResponse resetResponseInputStream(HttpRpcResponse response, final HttpEntity entity, final InputStream newInputStream) {
        if (newInputStream != null) {
            response = response.newBuilder()
                    .setEntity(new HttpEntity() {
                        @Override
                        public MimeType getContentType() {
                            return entity.getContentType();
                        }

                        @Override
                        public String getTransferEncoding() {
                            return entity.getTransferEncoding();
                        }

                        @Override
                        public Charset getCharset() {
                            return entity.getCharset();
                        }

                        @Override
                        public InputStream getContent() throws IOException {
                            return newInputStream;
                        }

                        @Override
                        public long getContentLength() throws IOException {
                            return entity.getContentLength();
                        }

                        @Override
                        public void writeTo(OutputStream out) throws IOException {
                            entity.writeTo(out);
                        }

                        @Override
                        public void close() throws IOException {
                            entity.close();
                        }
                    })
                    .build();
        }

        return response;

    }


    /**
     * 新的mock 接口是否有数据
     *
     * @return boolean
     */
    private boolean newResponseHasData(HttpRpcResponse response) throws Exception {
        //这里不能直接使用response.body().string()的方式输出日志
        //因为response.body().string()之后，response中的流会被关闭，程序会报错，我们需要创建出一
        //个新的response给应用层处理
        if (response.getEntity() != null && response.getEntity().getContent() != null) {
            return true;
        }
        return false;
    }


}
