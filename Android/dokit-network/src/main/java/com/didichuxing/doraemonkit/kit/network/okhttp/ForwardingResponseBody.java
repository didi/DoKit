package com.didichuxing.doraemonkit.kit.network.okhttp;

import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.BufferedSource;
import okio.Okio;

public class ForwardingResponseBody extends ResponseBody {
    private final ResponseBody mBody;
    private final BufferedSource mInterceptedSource;

    public ForwardingResponseBody(ResponseBody body, InputStream interceptedStream) {
        mBody = body;
        mInterceptedSource = Okio.buffer(Okio.source(interceptedStream));
    }

    @Override
    public MediaType contentType() {
        return mBody.contentType();
    }

    @Override
    public long contentLength() {
        return mBody.contentLength();
    }

    @Override
    public BufferedSource source() {
        return mInterceptedSource;
    }
}