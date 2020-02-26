package com.didichuxing.doraemonkit.kit.network.okhttp.interceptor;


import com.didichuxing.doraemonkit.kit.network.NetworkManager;
import com.didichuxing.doraemonkit.kit.network.bean.NetworkRecord;
import com.didichuxing.doraemonkit.kit.network.core.DefaultResponseHandler;
import com.didichuxing.doraemonkit.kit.network.core.NetworkInterpreter;
import com.didichuxing.doraemonkit.kit.network.core.RequestBodyHelper;
import com.didichuxing.doraemonkit.kit.network.okhttp.ForwardingResponseBody;
import com.didichuxing.doraemonkit.kit.network.okhttp.OkHttpInspectorRequest;
import com.didichuxing.doraemonkit.kit.network.okhttp.OkHttpInspectorResponse;
import com.didichuxing.doraemonkit.util.LogHelper;

import java.io.IOException;
import java.io.InputStream;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * 抓包拦截器
 */
public class DoraemonInterceptor implements Interceptor {
    public static final String TAG = "DoraemonInterceptor";

    private final NetworkInterpreter mNetworkInterpreter = NetworkInterpreter.get();

    @Override
    public Response intercept(Chain chain) throws IOException {
        LogHelper.i(TAG,"=====DoraemonInterceptor====");
        if (!NetworkManager.isActive()) {
            Request request = chain.request();
            return chain.proceed(request);
        }
        int requestId = mNetworkInterpreter.nextRequestId();

        Request request = chain.request();

        RequestBodyHelper requestBodyHelper = new RequestBodyHelper();
        OkHttpInspectorRequest inspectorRequest =
                new OkHttpInspectorRequest(requestId, request, requestBodyHelper);
        NetworkRecord record = mNetworkInterpreter.createRecord(requestId, inspectorRequest);
        Response response;
        try {
            response = chain.proceed(request);
        } catch (IOException e) {
            mNetworkInterpreter.httpExchangeFailed(requestId, e.toString());
            throw e;
        }

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


}