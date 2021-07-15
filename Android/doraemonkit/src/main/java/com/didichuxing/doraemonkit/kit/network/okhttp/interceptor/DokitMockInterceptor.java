package com.didichuxing.doraemonkit.kit.network.okhttp.interceptor;


import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.didichuxing.doraemonkit.util.ActivityUtils;
import com.didichuxing.doraemonkit.util.EncodeUtils;
import com.didichuxing.doraemonkit.util.TimeUtils;
import com.didichuxing.doraemonkit.util.ToastUtils;
import com.didichuxing.doraemonkit.kit.core.DoKitManager;
import com.didichuxing.doraemonkit.kit.health.AppHealthInfoUtil;
import com.didichuxing.doraemonkit.kit.health.model.AppHealthInfo;
import com.didichuxing.doraemonkit.kit.network.NetworkManager;
import com.didichuxing.doraemonkit.kit.network.okhttp.InterceptorUtil;
import com.didichuxing.doraemonkit.kit.network.room_db.DokitDbManager;
import com.didichuxing.doraemonkit.kit.network.room_db.MockInterceptApiBean;
import com.didichuxing.doraemonkit.kit.network.room_db.MockTemplateApiBean;
import com.didichuxing.doraemonkit.util.DoKitCommUtil;
import com.didichuxing.doraemonkit.util.LogHelper;

import org.json.JSONObject;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * @author jintai
 * @desc: 接口mock拦截器
 */
public class DokitMockInterceptor extends AbsDoKitInterceptor {


    @NonNull
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request oldRequest = chain.request();
        Response oldResponse = chain.proceed(oldRequest);
        String contentType = oldResponse.header("Content-Type");
        //如果是图片则不进行拦截
        if (InterceptorUtil.isImg(contentType)) {
            return oldResponse;
        }
        HttpUrl url = oldRequest.url();
        String host = url.host();
        //如果是mock平台的接口则不进行拦截
        if (host.equalsIgnoreCase(NetworkManager.MOCK_HOST)) {
            return oldResponse;
        }

        //path  /test/upload/img
        String path = URLDecoder.decode(url.encodedPath(), "utf-8");
        String queries = url.query();
        String jsonQuery = transformQuery(queries);
        String jsonRequestBody = transformRequestBody(oldRequest.body());
        //LogHelper.i(TAG, "realJsonQuery===>" + jsonQuery);
        //LogHelper.i(TAG, "realJsonRequestBody===>" + jsonRequestBody);
        String interceptMatchedId = DokitDbManager.getInstance().isMockMatched(path, jsonQuery, jsonRequestBody, DokitDbManager.MOCK_API_INTERCEPT, DokitDbManager.FROM_SDK_OTHER);
        String templateMatchedId = DokitDbManager.getInstance().isMockMatched(path, jsonQuery, jsonRequestBody, DokitDbManager.MOCK_API_TEMPLATE, DokitDbManager.FROM_SDK_OTHER);
        try {
            //网络的健康体检功能 统计流量大小
            if (DoKitManager.APP_HEALTH_RUNNING) {
                addNetWokInfoInAppHealth(oldRequest, oldResponse);
            }

            //是否命中拦截规则
            if (!TextUtils.isEmpty(interceptMatchedId)) {
                return matchedInterceptRule(url, path, interceptMatchedId, templateMatchedId, oldRequest, oldResponse, chain);
            }

            //是否命中模板规则
            matchedTemplateRule(oldResponse, path, templateMatchedId);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return oldResponse;
    }


    /**
     * 将request query 转化成json字符串
     *
     * @return
     */
    private String transformQuery(String query) {
        String json = "";
        if (TextUtils.isEmpty(query)) {
            return json;
        }

        try {
            //query 类似 ccc=ccc&ddd=ddd
            json = DoKitCommUtil.param2Json(EncodeUtils.urlDecode(query));
            //测试是否是json字符串
            new JSONObject(json);
        } catch (Exception e) {
            //e.printStackTrace();
            json = DokitDbManager.IS_NOT_NORMAL_QUERY_PARAMS;
            //LogHelper.e(TAG, "===query json====>" + json);
        }

        return json;
    }


    /**
     * 将request body 转化成json字符串
     *
     * @return
     */
    private String transformRequestBody(RequestBody requestBody) {
        //form :"application/x-www-form-urlencoded"
        //json :"application/json;"
        String json = "";
        if (requestBody == null || requestBody.contentType() == null) {
            return json;
        }

        try {
            String strBody = EncodeUtils.urlDecode(DoKitCommUtil.requestBodyToString(requestBody));
            if (TextUtils.isEmpty(strBody)) {
                return "";
            }

            if (requestBody.contentType().toString().toLowerCase().contains(DokitDbManager.MEDIA_TYPE_FORM)) {
                String form = strBody;
                //类似 ccc=ccc&ddd=ddd
                json = DoKitCommUtil.param2Json(form);
                //测试是否是json字符串
                new JSONObject(json);
            } else if (requestBody.contentType().toString().toLowerCase().contains(DokitDbManager.MEDIA_TYPE_JSON)) {
                json = strBody;
                //测试是否是json字符串
                new JSONObject(json);
            } else if (requestBody.contentType().toString().toLowerCase().contains(DokitDbManager.MEDIA_TYPE_PLAIN)) {
                json = strBody;
                //测试是否是json字符串
                try {
                    new JSONObject(json);
                } catch (Exception e) {
                    //类似 ccc=ccc&ddd=ddd
                    json = DoKitCommUtil.param2Json(json);
                    if (json.equals("{}")) {
                        json = DokitDbManager.IS_NOT_NORMAL_BODY_PARAMS;
                    }
                }
            } else {
                json = DokitDbManager.IS_NOT_NORMAL_BODY_PARAMS;
            }
        } catch (Exception e) {
            //e.printStackTrace();
            json = "";
            LogHelper.e(getTAG(), "===body json====>" + json);
        }

        return json;
    }


    /**
     * 动态添加网络拦截
     *
     * @param request
     * @param response
     */
    private void addNetWokInfoInAppHealth(@NonNull Request request, @NonNull Response response) {
        try {
            if (ActivityUtils.getTopActivity() == null) {
                return;
            }

            long upSize = -1;
            long downSize = -1;
            if (request.body() != null) {
                upSize = request.body().contentLength();
            }
            if (response.body() != null) {
                ResponseBody peekBody = response.peekBody(Long.MAX_VALUE);
                downSize = peekBody.bytes().length;
            }


            if (upSize < 0 && downSize < 0) {
                return;
            }

            upSize = upSize > 0 ? upSize : 0;
            downSize = downSize > 0 ? downSize : 0;

            String activityName = ActivityUtils.getTopActivity().getClass().getCanonicalName();
            AppHealthInfo.DataBean.NetworkBean networkBean = AppHealthInfoUtil.getInstance().getNetWorkInfo(activityName);
            AppHealthInfo.DataBean.NetworkBean.NetworkValuesBean networkValuesBean = new AppHealthInfo.DataBean.NetworkBean.NetworkValuesBean();
            networkValuesBean.setCode("" + response.code());

            networkValuesBean.setUp("" + upSize);
            networkValuesBean.setDown("" + downSize);
            networkValuesBean.setMethod(request.method());
            networkValuesBean.setTime("" + TimeUtils.getNowMills());
            networkValuesBean.setUrl(request.url().toString());
            if (networkBean == null) {
                networkBean = new AppHealthInfo.DataBean.NetworkBean();
                networkBean.setPage(activityName);
                List<AppHealthInfo.DataBean.NetworkBean.NetworkValuesBean> networkValuesBeans = new ArrayList<>();
                networkValuesBeans.add(networkValuesBean);
                networkBean.setValues(networkValuesBeans);
                AppHealthInfoUtil.getInstance().addNetWorkInfo(networkBean);
            } else {
                List<AppHealthInfo.DataBean.NetworkBean.NetworkValuesBean> networkValuesBeans = networkBean.getValues();
                if (networkValuesBeans == null) {
                    networkValuesBeans = new ArrayList<>();
                    networkValuesBeans.add(networkValuesBean);
                    networkBean.setValues(networkValuesBeans);
                } else {
                    networkValuesBeans.add(networkValuesBean);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

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
        MockInterceptApiBean interceptApiBean = (MockInterceptApiBean) DokitDbManager.getInstance().getInterceptApiByIdInMap(path, interceptMatchedId, DokitDbManager.FROM_SDK_OTHER);
        if (interceptApiBean == null) {
            matchedTemplateRule(oldResponse, path, templateMatchedId);
            return oldResponse;
        }
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
        StringBuilder sb = new StringBuilder();
        String newUrl;
        if (NetworkManager.MOCK_SCHEME_HTTP.contains(scheme.toLowerCase())) {
            newUrl = sb.append(NetworkManager.MOCK_SCHEME_HTTP).append(NetworkManager.MOCK_HOST).append("/api/app/scene/").append(selectedSceneId).toString();
        } else {
            newUrl = sb.append(NetworkManager.MOCK_SCHEME_HTTPS).append(NetworkManager.MOCK_HOST).append("/api/app/scene/").append(selectedSceneId).toString();
        }

        //LogHelper.i("MOCK_INTERCEPT", "path===>" + path + "  newUrl=====>" + newUrl);

        Request newRequest = new Request.Builder()
                .method("GET", null)
                .url(newUrl).build();
        //需要提前关闭数据流 不然在某些场景下会报错
        oldResponse.close();
        Response newResponse = chain.proceed(newRequest);
        if (newResponse.code() == 200) {
            //拦截命中提示
            ToastUtils.showShort("接口别名:==" + interceptApiBean.getMockApiName() + "==已被拦截");
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
        MockTemplateApiBean templateApiBean = (MockTemplateApiBean) DokitDbManager.getInstance().getTemplateApiByIdInMap(path, templateMatchedId, DokitDbManager.FROM_SDK_OTHER);
        if (templateApiBean == null) {
            return;
        }
        //LogHelper.i("MOCK_TEMPLATE", "path=====>" + path + "isOpen===>" + templateApiBean.isOpen());
        if (templateApiBean.isOpen()) {
            //保存老的response 数据到数据库
            saveResponse2DB(oldResponse, templateApiBean);
        }
    }


    /**
     * 保存匹配中的数据到本地数据库
     *
     * @param response
     * @param mockApi
     * @throws Exception
     */
    private void saveResponse2DB(Response response, MockTemplateApiBean mockApi) throws Exception {
        if (response.code() != 200) {
            return;
        }

        if (response.body() == null) {
            return;
        }


        String host = response.request().url().host();
        //LogHelper.i(TAG, "host====>" + host);
        //这里不能直接使用response.body().string()的方式输出日志
        //因为response.body().string()之后，response中的流会被关闭，程序会报错，我们需要创建出一
        //个新的response给应用层处理
        ResponseBody responseBody = response.peekBody(Long.MAX_VALUE);

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
        //拦截命中提示
        ToastUtils.showShort("模板别名:==" + mockApi.getMockApiName() + "==已被保存");
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
        return true;
    }


}