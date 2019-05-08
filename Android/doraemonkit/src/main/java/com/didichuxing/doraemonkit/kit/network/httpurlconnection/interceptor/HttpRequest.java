package com.didichuxing.doraemonkit.kit.network.httpurlconnection.interceptor;

import java.io.OutputStream;
import java.net.HttpURLConnection;

/**
 * @date: 2019/3/12
 * @desc: 请求结构体
 */
public class HttpRequest {
    private final int mId;
    private final HttpURLConnection mURLConnection;
    private OutputStream mOutputStream;

    public HttpRequest(int id, HttpURLConnection urlConnection) {
        mURLConnection = urlConnection;
        mId = id;
    }

    public HttpURLConnection getURLConnection() {
        return mURLConnection;
    }

    public int getId() {
        return mId;
    }

    public OutputStream getOutputStream() {
        return mOutputStream;
    }

    public void setOutputStream(OutputStream outputStream) {
        mOutputStream = outputStream;
    }
}
