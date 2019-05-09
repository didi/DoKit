package com.didichuxing.doraemonkit.kit.network.httpurlconnection.interceptor;

import java.io.InputStream;
import java.net.HttpURLConnection;

/**
 * @date: 2019/3/12
 * @desc: 响应体
 */
public class HttpResponse {
    private final int mId;
    private final HttpURLConnection mURLConnection;
    private int mStatusCode;
    private InputStream mInputStream;

    public HttpResponse(int id, HttpURLConnection urlConnection) {
        mURLConnection = urlConnection;
        mId = id;
    }

    public HttpURLConnection getURLConnection() {
        return mURLConnection;
    }

    public int getId() {
        return mId;
    }

    public int getStatusCode() {
        return mStatusCode;
    }

    public void setStatusCode(int statusCode) {
        mStatusCode = statusCode;
    }

    public InputStream getInputStream() {
        return mInputStream;
    }

    public void setInputStream(InputStream inputStream) {
        mInputStream = inputStream;
    }
}
