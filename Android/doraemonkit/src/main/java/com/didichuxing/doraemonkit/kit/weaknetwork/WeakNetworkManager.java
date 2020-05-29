package com.didichuxing.doraemonkit.kit.weaknetwork;

import android.os.SystemClock;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.concurrent.atomic.AtomicBoolean;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.internal.http.RealResponseBody;
import okio.Okio;
import okio.Source;

/**
 * Created by xiandanin on 2019-05-09 16:30
 */
public class WeakNetworkManager {
    public static final int TYPE_OFF_NETWORK = 0;
    public static final int TYPE_TIMEOUT = 1;
    public static final int TYPE_SPEED_LIMIT = 2;

    public static final int DEFAULT_TIMEOUT_MILLIS = 2000;
    public static final int DEFAULT_REQUEST_SPEED = 1;
    public static final int DEFAULT_RESPONSE_SPEED = 1;

    private int mType = TYPE_OFF_NETWORK;
    private long mTimeOutMillis = DEFAULT_TIMEOUT_MILLIS;
    private long mRequestSpeed = DEFAULT_REQUEST_SPEED;
    private long mResponseSpeed = DEFAULT_RESPONSE_SPEED;

    private AtomicBoolean mIsActive = new AtomicBoolean(false);

    private static class Holder {
        private static WeakNetworkManager INSTANCE = new WeakNetworkManager();
    }

    public static WeakNetworkManager get() {
        return WeakNetworkManager.Holder.INSTANCE;
    }

    public boolean isActive() {
        return mIsActive.get();
    }

    public void setActive(boolean isActive) {
        mIsActive.set(isActive);
    }


    public void setParameter(long timeOutMillis, long requestSpeed, long responseSpeed) {
        if (timeOutMillis > 0) {
            mTimeOutMillis = timeOutMillis;
        }
        mRequestSpeed = requestSpeed;
        mResponseSpeed = responseSpeed;
    }

    public void setType(int type) {
        mType = type;
    }

    public int getType() {
        return mType;
    }

    public long getTimeOutMillis() {
        return mTimeOutMillis;
    }

    public long getRequestSpeed() {
        return mRequestSpeed;
    }

    public long getResponseSpeed() {
        return mResponseSpeed;
    }

    /**
     * 模拟断网
     */
    public Response simulateOffNetwork(Interceptor.Chain chain) throws IOException {
        final Response response = chain.proceed(chain.request());
        ResponseBody responseBody = ResponseBody.create(response.body().contentType(), "");
        Response newResponse = response.newBuilder()
                .code(400)
                .message(String.format("Unable to resolve host %s: No address associated with hostname", chain.request().url().host()))
                .body(responseBody)
                .build();
        return newResponse;
    }

    /**
     * 模拟超时
     *
     * @param chain url
     */
    public Response simulateTimeOut(Interceptor.Chain chain) throws IOException {
        SystemClock.sleep(mTimeOutMillis);
        final Response response = chain.proceed(chain.request());
        ResponseBody responseBody = ResponseBody.create(response.body().contentType(), "");
        Response newResponse = response.newBuilder()
                .code(400)
                .message(String.format("failed to connect to %s  after %dms", chain.request().url().host(), mTimeOutMillis))
                .body(responseBody)
                .build();
        return newResponse;
    }

    /**
     * 限速
     */
    public Response simulateSpeedLimit(Interceptor.Chain chain) throws IOException {
        Request request = chain.request();
        final RequestBody body = request.body();
        if (body != null) {
            //大于0使用限速的body 否则使用原始body
            final RequestBody requestBody = mRequestSpeed > 0 ? new SpeedLimitRequestBody(mRequestSpeed, body) : body;
            request = request.newBuilder().method(request.method(), requestBody).build();
        }
        final Response response = chain.proceed(request);
        //大于0使用限速的body 否则使用原始body
        final ResponseBody responseBody = response.body();
        final ResponseBody newResponseBody = mResponseSpeed > 0 ? new SpeedLimitResponseBody(mResponseSpeed, responseBody) : responseBody;
        return response.newBuilder().body(newResponseBody).build();
    }
}
