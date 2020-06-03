package com.didichuxing.doraemonkit.kit.network.okhttp.interceptor;


import android.text.TextUtils;

import com.didichuxing.doraemonkit.constant.DokitConstant;
import com.didichuxing.doraemonkit.kit.network.NetworkManager;
import com.didichuxing.doraemonkit.kit.network.bean.NetworkRecord;
import com.didichuxing.doraemonkit.kit.network.bean.WhiteHostBean;
import com.didichuxing.doraemonkit.kit.network.core.DefaultResponseHandler;
import com.didichuxing.doraemonkit.kit.network.core.NetworkInterpreter;
import com.didichuxing.doraemonkit.kit.network.core.RequestBodyHelper;
import com.didichuxing.doraemonkit.kit.network.core.ResourceType;
import com.didichuxing.doraemonkit.kit.network.core.ResourceTypeHelper;
import com.didichuxing.doraemonkit.kit.network.okhttp.ForwardingResponseBody;
import com.didichuxing.doraemonkit.kit.network.okhttp.InterceptorUtil;
import com.didichuxing.doraemonkit.kit.network.okhttp.OkHttpInspectorRequest;
import com.didichuxing.doraemonkit.kit.network.okhttp.OkHttpInspectorResponse;
import com.didichuxing.doraemonkit.util.LogHelper;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * 抓包拦截器
 */
public class DoraemonInterceptor implements Interceptor {
    public static final String TAG = "DoraemonInterceptor";

    private final NetworkInterpreter mNetworkInterpreter = NetworkInterpreter.get();

    @NotNull
    @Override
    public Response intercept(Chain chain) throws IOException {
        if (!NetworkManager.isActive()) {
            Request request = chain.request();
            try {
                return chain.proceed(request);
            } catch (Exception e) {
                ResponseBody responseBody = ResponseBody.create(MediaType.parse("application/json;charset=utf-8"), "" + e.getMessage());
                return new Response.Builder()
                        .code(400)
                        .message(String.format("%s==>Exception:%s", chain.request().url().host(), e.getMessage()))
                        .request(request)
                        .body(responseBody)
                        .protocol(Protocol.HTTP_1_1)
                        .build();
            }
        }

        Request request = chain.request();
        int requestId = mNetworkInterpreter.nextRequestId();
        Response response;
        try {
            response = chain.proceed(request);
        } catch (Exception e) {
            LogHelper.e(TAG, "e===>" + e.getMessage());
            mNetworkInterpreter.httpExchangeFailed(requestId, e.toString());
            ResponseBody responseBody = ResponseBody.create(MediaType.parse("application/json;charset=utf-8"), "" + e.getMessage());
            return new Response.Builder()
                    .code(400)
                    .message(String.format("%s==>Exception:%s", chain.request().url().host(), e.getMessage()))
                    .request(request)
                    .body(responseBody)
                    .protocol(Protocol.HTTP_1_1)
                    .build();
        }

        String strContentType = response.header("Content-Type");
        //如果是图片则不进行拦截
        if (InterceptorUtil.isImg(strContentType)) {
            return response;
        }
        //白名单过滤
        if (!matchWhiteHost(request)) {
            return response;
        }


        RequestBodyHelper requestBodyHelper = new RequestBodyHelper();
        OkHttpInspectorRequest inspectorRequest =
                new OkHttpInspectorRequest(requestId, request, requestBodyHelper);
        NetworkRecord record = mNetworkInterpreter.createRecord(requestId, inspectorRequest);

        NetworkInterpreter.InspectorResponse inspectorResponse = new OkHttpInspectorResponse(
                requestId,
                request,
                response);
        mNetworkInterpreter.fetchResponseInfo(record, inspectorResponse);

        ResponseBody body = response.body();
        InputStream responseStream = null;
        MediaType contentType = null;
        if (body != null) {
            contentType = body.contentType();
            responseStream = body.byteStream();
        }

        responseStream = mNetworkInterpreter.interpretResponseStream(
                contentType != null ? contentType.toString() : null,
                responseStream,
                new DefaultResponseHandler(mNetworkInterpreter, requestId, record));
        if (responseStream != null) {
            response = response.newBuilder()
                    .body(new ForwardingResponseBody(body, responseStream))
                    .build();
        }

        return response;
    }

    /**
     * 是否命中白名单规则
     *
     * @return bool
     */
    private boolean matchWhiteHost(Request request) {
        List<WhiteHostBean> whiteHostBeans = DokitConstant.WHITE_HOSTS;
        if (whiteHostBeans.isEmpty()) {
            return true;
        }

        for (WhiteHostBean whiteHostBean : whiteHostBeans) {
            if (TextUtils.isEmpty(whiteHostBean.getHost())) {
                continue;
            }
            String realHost = request.url().host();
            //正则判断
            if (whiteHostBean.getHost().equalsIgnoreCase(realHost)) {
                return true;
            }
        }

        return false;
    }


}