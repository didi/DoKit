package com.didichuxing.doraemonkit.kit.network.okhttp.interceptor;


import android.text.TextUtils;

import com.didichuxing.doraemonkit.kit.network.NetworkManager;
import com.didichuxing.doraemonkit.kit.network.core.ResourceTypeHelper;
import com.didichuxing.doraemonkit.kit.network.room_db.DokitDbManager;
import com.didichuxing.doraemonkit.kit.network.room_db.MockInterceptApiBean;
import com.didichuxing.doraemonkit.kit.network.room_db.MockTemplateApiBean;
import com.didichuxing.doraemonkit.util.LogHelper;

import java.io.IOException;
import java.net.URLDecoder;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * @author jintai
 * @desc: 接口mock拦截器
 */
public class MockInterceptor implements Interceptor {
    public static final String TAG = "MockInterceptor";

    private ResourceTypeHelper mResourceTypeHelper;

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request oldRequest = chain.request();
        Response oldResponse = chain.proceed(oldRequest);

        HttpUrl url = oldRequest.url();
        String host = url.host();
        //如果是mock平台的接口则不进行拦截
        if (host.equalsIgnoreCase(NetworkManager.MOCK_HOST)) {
            return oldResponse;
        }
        //path  /test/upload/img
        String path = URLDecoder.decode(url.encodedPath(), "utf-8");
        String queries = url.query();
        String interceptMatchedId = DokitDbManager.getInstance().isMockMatched(path, queries, DokitDbManager.MOCK_API_INTERCEPT);
        String templateMatchedId = DokitDbManager.getInstance().isMockMatched(path, queries, DokitDbManager.MOCK_API_TEMPLATE);
        try {
            //是否命中拦截规则
            if (!TextUtils.isEmpty(interceptMatchedId)) {
                return matchedInterceptRule(url, path, interceptMatchedId, templateMatchedId, oldRequest, oldResponse, chain);
            }

            //是否命中模板规则
            matchedTemplateRule(oldResponse, path, templateMatchedId);

        } catch (Exception e) {
            e.printStackTrace();
            return oldResponse;
        }
        return oldResponse;
    }


    /**
     * 命中拦截规则
     * 返回新的response
     *
     * @param interceptMatchedId
     * @return
     */
    private Response matchedInterceptRule(HttpUrl url, String path, String interceptMatchedId, String templateMatchedId, Request oldRequest, Response oldResponse, Chain chain) throws Exception {
        //判断是否需要重定向数据接口
        //http https
        String scheme = url.scheme();
        MockInterceptApiBean interceptApiBean = (MockInterceptApiBean) DokitDbManager.getInstance().getInterceptApiByIdInMap(path, interceptMatchedId);

        String selectedSceneId = interceptApiBean.getSelectedSceneId();
        //开关是否被打开
        if (!interceptApiBean.isOpen()) {
            matchedTemplateRule(oldResponse, path, templateMatchedId);
            return oldResponse;
        }

        //判断是否有选中的场景
        if (TextUtils.isEmpty(selectedSceneId)) {
            matchedTemplateRule(oldResponse, path, templateMatchedId);
            return oldResponse;
        }
        StringBuffer sb = new StringBuffer();
        String newUrl;
        if (NetworkManager.MOCK_SCHEME_HTTP.contains(scheme.toLowerCase())) {
            newUrl = sb.append(NetworkManager.MOCK_SCHEME_HTTP).append(NetworkManager.MOCK_HOST).append("/api/app/scene/").append(selectedSceneId).toString();
        } else {
            newUrl = sb.append(NetworkManager.MOCK_SCHEME_HTTPS).append(NetworkManager.MOCK_HOST).append("/api/app/scene/").append(selectedSceneId).toString();
        }

        Request newRequest = oldRequest.newBuilder()
                .url(newUrl).build();
        Response newResponse = chain.proceed(newRequest);
        if (newResponse.code() == 200) {
            //判断新的response是否有数据
            if (newResponseHasData(newResponse)) {
                matchedTemplateRule(newResponse, path, templateMatchedId);
                return newResponse;
            } else {
                matchedTemplateRule(oldResponse, path, templateMatchedId);
                return oldResponse;
            }
        }
        matchedTemplateRule(oldResponse, path, templateMatchedId);
        return oldResponse;
    }

    /**
     * 命中模板规则
     * 保存正常接口的response到数据库
     *
     * @return
     */
    private void matchedTemplateRule(Response oldResponse, String path, String templateMatchedId) throws Exception {
        //命中模板规则
        if (TextUtils.isEmpty(templateMatchedId)) {
            return;
        }
        MockTemplateApiBean templateApiBean = (MockTemplateApiBean) DokitDbManager.getInstance().getTemplateApiByIdInMap(path, templateMatchedId);
        if (templateApiBean.isOpen()) {
            //保存老的response 数据到数据库
            saveRespnse2DB(oldResponse, templateApiBean);
        }
    }


    private ResourceTypeHelper getResourceTypeHelper() {
        if (mResourceTypeHelper == null) {
            mResourceTypeHelper = new ResourceTypeHelper();
        }
        return mResourceTypeHelper;
    }

    /**
     * 保存匹配中的数据到本地数据库
     *
     * @param response
     * @param mockApi
     * @throws Exception
     */
    private void saveRespnse2DB(Response response, MockTemplateApiBean mockApi) throws Exception {
        if (response.code() != 200) {
            return;
        }

        if (response.body() == null || response.body().contentLength() <= 0) {
            return;
        }


        String host = response.request().url().host();
        LogHelper.i(TAG, "host====>" + host);
        //这里不能直接使用response.body().string()的方式输出日志
        //因为response.body().string()之后，response中的流会被关闭，程序会报错，我们需要创建出一
        //个新的response给应用层处理
        ResponseBody responseBody = response.peekBody(1024 * 1024);

        String strResponseBody = responseBody.string();
        if (TextUtils.isEmpty(strResponseBody)) {
            return;
        }

        if (host.equals(NetworkManager.MOCK_HOST)) {
            mockApi.setResponseFrom(MockTemplateApiBean.RESPONSE_FROM_MOCK);
        } else {
            mockApi.setResponseFrom(MockTemplateApiBean.RESPONSE_FROM_REAL);
        }

        mockApi.setStrResponse(strResponseBody);
        //更新本地数据库
        DokitDbManager.getInstance().updateTemplateApi(mockApi);
    }

    /**
     * 新的mock 接口是否有数据
     *
     * @return
     */
    private boolean newResponseHasData(Response response) throws Exception {
        //这里不能直接使用response.body().string()的方式输出日志
        //因为response.body().string()之后，response中的流会被关闭，程序会报错，我们需要创建出一
        //个新的response给应用层处理
        if (response.body() == null) {
            return false;
        }
        return response.body().contentLength() > 0;
    }


}