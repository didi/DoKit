package com.didichuxing.doraemonkit.kit.network.okhttp;

import com.didichuxing.doraemonkit.kit.network.core.NetworkInterpreter;
import com.didichuxing.doraemonkit.kit.network.core.RequestBodyHelper;

import java.io.IOException;
import java.io.OutputStream;

import okhttp3.Request;
import okhttp3.RequestBody;
import okio.BufferedSink;
import okio.Okio;

public class OkHttpInspectorRequest implements NetworkInterpreter.InspectorRequest {
    private final int mRequestId;
    private final Request mRequest;
    private RequestBodyHelper mRequestBodyHelper;

    public OkHttpInspectorRequest(
            int requestId,
            Request request,
            RequestBodyHelper requestBodyHelper) {
        mRequestId = requestId;
        mRequest = request;
        mRequestBodyHelper = requestBodyHelper;
    }

    @Override
    public int id() {
        return mRequestId;
    }


    @Override
    public String url() {
        return mRequest.url().toString();
    }

    @Override
    public String method() {
        return mRequest.method();
    }

    @Override
    public byte[] body() throws IOException {
        RequestBody body = mRequest.body();
        if (body == null) {
            return null;
        }
        OutputStream out = mRequestBodyHelper.createBodySink(firstHeaderValue("Content-Encoding"));
        BufferedSink bufferedSink = Okio.buffer(Okio.sink(out));
        try {
            body.writeTo(bufferedSink);
        } finally {
            bufferedSink.close();
        }
        return mRequestBodyHelper.getDisplayBody();
    }

    @Override
    public int headerCount() {
        return mRequest.headers().size();
    }

    @Override
    public String headerName(int index) {
        return mRequest.headers().name(index);
    }

    @Override
    public String headerValue(int index) {
        return mRequest.headers().value(index);
    }

    @Override
    public String firstHeaderValue(String name) {
        return mRequest.header(name);
    }
}
