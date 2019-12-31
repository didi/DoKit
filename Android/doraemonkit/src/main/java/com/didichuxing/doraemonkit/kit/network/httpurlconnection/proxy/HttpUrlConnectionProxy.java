package com.didichuxing.doraemonkit.kit.network.httpurlconnection.proxy;

import android.util.Log;

import com.didichuxing.doraemonkit.kit.network.NetworkManager;
import com.didichuxing.doraemonkit.kit.network.httpurlconnection.HttpChainFacade;
import com.didichuxing.doraemonkit.kit.network.httpurlconnection.HttpRequest;
import com.didichuxing.doraemonkit.kit.network.httpurlconnection.HttpResponse;
import com.didichuxing.doraemonkit.kit.network.httpurlconnection.interceptor.DKInterceptor;
import com.didichuxing.doraemonkit.kit.network.httpurlconnection.interceptor.HttpMonitorInterceptor;
import com.didichuxing.doraemonkit.kit.network.httpurlconnection.interceptor.LargePictureInterceptor;
import com.didichuxing.doraemonkit.kit.network.httpurlconnection.interceptor.MockInterceptor;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @desc: UrlConnection代理类，用以解析请求
 */
public class HttpUrlConnectionProxy extends HttpURLConnection {
    private final String TAG = "HttpUrlConnectionProxy";
    private final boolean DEBUG = true;
    private final HttpURLConnection mSourceConnection;
    private List<DKInterceptor> mInterceptors = new ArrayList<>();

    private final HttpRequest mHttpRequest;
    private final HttpResponse mHttpResponse;

    private final HttpChainFacade mHttpChainFacade;

    public HttpUrlConnectionProxy(HttpURLConnection con, boolean isMock) {
        super(con.getURL());
        mSourceConnection = con;
        //mock
        if (isMock) {
            mInterceptors.add(new MockInterceptor());
        }
        if (NetworkManager.isActive()) {
            mInterceptors.add(new HttpMonitorInterceptor());
            //https的大图检测拦截器
            mInterceptors.add(new LargePictureInterceptor());
        }


        mHttpRequest = new HttpRequest(con);
        mHttpResponse = new HttpResponse(con);

        mHttpChainFacade = new HttpChainFacade(mInterceptors);

    }


    @Override
    public void addRequestProperty(String field, String newValue) {
        mSourceConnection.addRequestProperty(field, newValue);
    }

    @Override
    public void disconnect() {
        // TODO Auto-generated method stub
        if (DEBUG) {
            Log.d(TAG, "disconnect. ");
        }
        mSourceConnection.disconnect();
    }


    @Override
    public boolean usingProxy() {
        return mSourceConnection.usingProxy();
    }

    @Override
    public void connect() throws IOException {
        preConnect();
        try {
            mSourceConnection.connect();
        } catch (IOException e) {
            throw e;
        }
    }

    /**
     *
     */
    public void preConnect() throws IOException {
        mHttpChainFacade.process(mHttpRequest);
    }

    public void postConnect(int statusCode) throws IOException {
        mHttpResponse.setStatusCode(statusCode);
        mHttpChainFacade.process(mHttpResponse);
    }

    @Override
    public boolean getAllowUserInteraction() {
        return mSourceConnection.getAllowUserInteraction();
    }

    @Override
    public int getConnectTimeout() {
        return mSourceConnection.getConnectTimeout();
    }

    @Override
    public Object getContent() throws IOException {
        return mSourceConnection.getContent();
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Object getContent(Class[] types) throws IOException {
        return mSourceConnection.getContent(types);
    }

    @Override
    public String getContentEncoding() {
        return mSourceConnection.getContentEncoding();
    }

    @Override
    public int getContentLength() {
        return mSourceConnection.getContentLength();
    }

    @Override
    public String getContentType() {
        return mSourceConnection.getContentType();
    }

    @Override
    public long getDate() {
        return mSourceConnection.getDate();
    }

    @Override
    public InputStream getErrorStream() {
        return mSourceConnection.getErrorStream();
    }

    @Override
    public long getHeaderFieldDate(String field, long defaultValue) {
        return mSourceConnection.getHeaderFieldDate(field, defaultValue);
    }

    @Override
    public boolean getInstanceFollowRedirects() {
        return mSourceConnection.getInstanceFollowRedirects();
    }

    @Override
    public java.security.Permission getPermission() throws IOException {
        return mSourceConnection.getPermission();
    }

    @Override
    public String getRequestMethod() {
        return mSourceConnection.getRequestMethod();
    }

    @Override
    public int getResponseCode() throws IOException {
        preConnect();
        int code = mSourceConnection.getResponseCode();
        postConnect(code);
        return code;
    }

    @Override
    public String getResponseMessage() throws IOException {
        return mSourceConnection.getResponseMessage();
    }

    @Override
    public void setChunkedStreamingMode(int chunkLength) {
        mSourceConnection.setChunkedStreamingMode(chunkLength);
    }

    @Override
    public void setFixedLengthStreamingMode(int contentLength) {
        mSourceConnection.setFixedLengthStreamingMode(contentLength);
    }

    @Override
    public void setInstanceFollowRedirects(boolean followRedirects) {
        mSourceConnection.setInstanceFollowRedirects(followRedirects);
    }

    @Override
    public void setRequestMethod(String method) throws ProtocolException {
        mSourceConnection.setRequestMethod(method);
    }

    @Override
    public boolean getDefaultUseCaches() {
        return mSourceConnection.getDefaultUseCaches();
    }

    @Override
    public boolean getDoInput() {
        return mSourceConnection.getDoInput();
    }

    @Override
    public boolean getDoOutput() {
        return mSourceConnection.getDoOutput();
    }

    @Override
    public long getExpiration() {
        return mSourceConnection.getExpiration();
    }

    @Override
    public String getHeaderField(int pos) {
        return mSourceConnection.getHeaderField(pos);
    }

    @Override
    public String getHeaderField(String key) {
        return mSourceConnection.getHeaderField(key);
    }

    @Override
    public int getHeaderFieldInt(String field, int defaultValue) {
        return mSourceConnection.getHeaderFieldInt(field, defaultValue);
    }

    @Override
    public String getHeaderFieldKey(int paramInt) {
        return mSourceConnection.getHeaderFieldKey(paramInt);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public Map<String, List<String>> getHeaderFields() {
        return mSourceConnection.getRequestProperties();
    }

    @Override
    public long getIfModifiedSince() {
        return mSourceConnection.getIfModifiedSince();
    }

    @Override
    public InputStream getInputStream() throws IOException {
        mHttpResponse.setInputStream(mSourceConnection.getInputStream());
        mHttpChainFacade.processStream(mHttpResponse);
        return mHttpResponse.getInputStream();
    }

    @Override
    public long getLastModified() {
        return mSourceConnection.getLastModified();
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        mHttpRequest.setOutputStream(mSourceConnection.getOutputStream());
        mHttpChainFacade.processStream(mHttpRequest);
        return mHttpRequest.getOutputStream();
    }

    @Override
    public int getReadTimeout() {
        return mSourceConnection.getReadTimeout();
    }

    @Override
    public Map<String, List<String>> getRequestProperties() {
        return mSourceConnection.getRequestProperties();
    }

    @Override
    public String getRequestProperty(String field) {
        return mSourceConnection.getRequestProperty(field);
    }

    @Override
    public URL getURL() {
        return mSourceConnection.getURL();
    }

    @Override
    public boolean getUseCaches() {
        return mSourceConnection.getUseCaches();
    }

    @Override
    public void setAllowUserInteraction(boolean newValue) {
        mSourceConnection.setAllowUserInteraction(newValue);
    }

    @Override
    public void setConnectTimeout(int timeout) {
        mSourceConnection.setConnectTimeout(timeout);
    }

    @Override
    public void setDefaultUseCaches(boolean newValue) {
        mSourceConnection.setDefaultUseCaches(newValue);
    }

    @Override
    public void setDoInput(boolean newValue) {
        mSourceConnection.setDoInput(newValue);
    }

    @Override
    public void setDoOutput(boolean newValue) {
        mSourceConnection.setDoOutput(newValue);
    }

    @Override
    public void setIfModifiedSince(long newValue) {
        mSourceConnection.setIfModifiedSince(newValue);
    }

    @Override
    public void setReadTimeout(int timeout) {
        mSourceConnection.setReadTimeout(timeout);
    }

    @Override
    public void setRequestProperty(String field, String newValue) {
        mSourceConnection.setRequestProperty(field, newValue);
    }

    @Override
    public void setUseCaches(boolean newValue) {
        mSourceConnection.setUseCaches(newValue);
    }

    @Override
    public String toString() {
        if (mSourceConnection == null) {
            return "this connection object is null";
        } else {
            return mSourceConnection.toString();
        }
    }
}
