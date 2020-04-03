package com.didichuxing.doraemonkit.kit.network.httpurlconnection;

import android.util.Log;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.Map;

/**
 *  2019/3/12
 * @desc: 响应体
 */
public class HttpResponse {
    private final String TAG = "HttpResponse";
    private final HttpURLConnection mURLConnection;
    private int mStatusCode;
    private InputStream mInputStream;

    public HttpResponse(HttpURLConnection urlConnection) {
        mURLConnection = urlConnection;
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

    public String getUrl() {
        return mURLConnection.getURL().toString();
    }

    public Map<String, List<String>> getHeaders() {
        try {
            return mURLConnection.getHeaderFields();
        } catch (Exception e) {
            Log.d(TAG, "get head exception", e);
            return null;
        }
    }

    public String getHeaderField(String key) {
        Map<String, List<String>> map = getHeaders();
        if (map == null) {
            return null;
        }
        List<String> fields = map.get(key);
        if (fields == null || fields.size() == 0) {
            return null;
        }
        return fields.get(fields.size() - 1);
    }
}
