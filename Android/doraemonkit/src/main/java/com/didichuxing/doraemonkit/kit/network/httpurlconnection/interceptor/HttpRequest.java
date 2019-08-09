package com.didichuxing.doraemonkit.kit.network.httpurlconnection.interceptor;

import android.util.Log;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.Map;

/**
 * @date: 2019/3/12
 * @desc: 请求结构体
 */
public class HttpRequest {
    private static final String TAG = "HttpRequest";
    private final HttpURLConnection mURLConnection;
    private OutputStream mOutputStream;

    public HttpRequest(HttpURLConnection urlConnection) {
        mURLConnection = urlConnection;
    }

    public String getUrl(){
        return mURLConnection.getURL().toString();
    }

    public String getMethod(){
        return mURLConnection.getRequestMethod();
    }

    public OutputStream getOutputStream() {
        return mOutputStream;
    }

    public Map<String, List<String>> getHeaders() {
        // connect参数不知道什么会被置为true，以防崩溃，这里直接try-catch住
        try {
            return mURLConnection.getRequestProperties();
        } catch (Exception e) {
            Log.d(TAG, "get head exception", e);
            return null;
        }
    }

    public void setOutputStream(OutputStream outputStream) {
        mOutputStream = outputStream;
    }
}
