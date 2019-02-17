package com.didichuxing.doraemonkit.kit.network.httpurlconnection;

import android.util.Log;
import android.util.Pair;

import com.didichuxing.doraemonkit.kit.network.bean.NetworkRecord;
import com.didichuxing.doraemonkit.kit.network.core.DefaultResponseHandler;
import com.didichuxing.doraemonkit.kit.network.core.NetworkInterpreter;
import com.didichuxing.doraemonkit.kit.network.core.RequestBodyHelper;
import com.didichuxing.doraemonkit.kit.network.stream.OutputStreamProxy;
import com.didichuxing.doraemonkit.kit.network.utils.StreamUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ProtocolException;
import java.net.URL;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLPeerUnverifiedException;

/**
 * @desc: UrlConnection代理类，用以解析请求
 */
public class HttpsUrlConnectionProxy extends HttpsURLConnection implements IStreamCompleteListener {
    private final String TAG = "HttpUrlConnectionProxy";
    private final boolean DEBUG = true;
    private final HttpsURLConnection mSourceConnection;
    private final NetworkInterpreter mInterpreter;
    private final int mRequestId;
    private RequestBodyHelper mRequestBodyHelper;
    private URLConnectionInspectorRequest mInspectorRequest;
    private NetworkRecord mRecord;

    public HttpsUrlConnectionProxy(HttpsURLConnection con) {
        super(con.getURL());
        mSourceConnection = con;
        mInterpreter = NetworkInterpreter.get();
        mRequestId = mInterpreter.nextRequestId();
    }

    @Override
    public String getCipherSuite() {
        return mSourceConnection.getCipherSuite();
    }

    @Override
    public Certificate[] getLocalCertificates() {
        return mSourceConnection.getLocalCertificates();
    }

    @Override
    public Certificate[] getServerCertificates() throws SSLPeerUnverifiedException {
        return mSourceConnection.getServerCertificates();
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
        if (DEBUG) {
            Log.d(TAG, "connect. ");
        }
        preConnect();
        try {
            mSourceConnection.connect();
        } catch (IOException e) {
            throw e;
        }
    }

    /**
     */
    public void preConnect() {
        if (mRecord != null) {
            return;
        }
        mRequestBodyHelper = new RequestBodyHelper();
        ArrayList<Pair<String, String>> header;
        // connect参数不知道什么会被置为true，以防崩溃，这里直接try-catch住
        try {
            header = StreamUtil.convertHeaders(getRequestProperties());
        } catch (Exception e) {
            Log.e(TAG, "get head exception", e);
            header = new ArrayList<>();
        }
        mInspectorRequest = new URLConnectionInspectorRequest(
                mRequestId,
                header,
                mSourceConnection,
                mRequestBodyHelper);
        mRecord = mInterpreter.createRecord(mRequestId, mInspectorRequest);
    }

    public void postConnect(int statusCode) throws IOException {
        if (mRecord == null) {
            return;
        }
        URLConnectionInspectorResponse response = new URLConnectionInspectorResponse(
                mRequestId,
                mSourceConnection,
                statusCode);
        mInterpreter.fetchResponseInfo(mRecord, response);
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
        return mSourceConnection.getHeaderFields();
    }

    @Override
    public long getIfModifiedSince() {
        return mSourceConnection.getIfModifiedSince();
    }

    @Override
    public InputStream getInputStream() throws IOException {
        if (mRecord != null) {
            InputStream responseStream = mInterpreter.interpretResponseStream(
                    getHeaderField("Content-Type"),
                    mSourceConnection.getInputStream(),
                    new DefaultResponseHandler(mInterpreter, mRequestId, mRecord));
            return responseStream;
        } else {
            return mSourceConnection.getInputStream();
        }
    }

    @Override
    public long getLastModified() {
        return mSourceConnection.getLastModified();
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        OutputStreamProxy outputStream;
        try {
            outputStream = new OutputStreamProxy(mSourceConnection.getOutputStream());
            outputStream.setStreamCompleteListener(this);
        } catch (IOException e) {
            throw e;
        }
        return outputStream;
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

    @Override
    public void onOutputStreamComplete(ByteArrayOutputStream outputStream) {
        if (mInspectorRequest != null && mRecord != null) {
            SimpleRequestEntity entity = new ByteArrayRequestEntity(outputStream.toByteArray());
            mInspectorRequest.setRequestEntity(entity);
            mInterpreter.fetRequestBody(mRecord, mInspectorRequest);
        }
    }

}
