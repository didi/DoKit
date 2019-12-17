package com.didichuxing.doraemonkit.kit.network.okhttp;

import com.didichuxing.doraemonkit.kit.network.core.NetworkInterpreter;

import okhttp3.Request;
import okhttp3.Response;

public class OkHttpInspectorResponse implements NetworkInterpreter.InspectorResponse {
    private final int mRequestId;
    private final Request mRequest;
    private final Response mResponse;

    public OkHttpInspectorResponse(
            int requestId,
            Request request,
            Response response) {
        mRequestId = requestId;
        mRequest = request;
        mResponse = response;
    }

    @Override
    public int requestId() {
        return mRequestId;
    }

    @Override
    public String url() {
        return mRequest.url().toString();
    }

    @Override
    public int statusCode() {
        return mResponse.code();
    }

    @Override
    public int headerCount() {
        return mResponse.headers().size();
    }

    @Override
    public String headerName(int index) {
        return mResponse.headers().name(index);
    }

    @Override
    public String headerValue(int index) {
        return mResponse.headers().value(index);
    }

    @Override
    public String firstHeaderValue(String name) {
        return mResponse.header(name);
    }
}

