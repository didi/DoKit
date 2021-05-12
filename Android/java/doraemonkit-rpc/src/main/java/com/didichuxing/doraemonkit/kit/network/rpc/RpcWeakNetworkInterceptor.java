package com.didichuxing.doraemonkit.kit.network.rpc;

import android.os.SystemClock;

import com.didichuxing.doraemonkit.kit.weaknetwork.RpcSpeedLimitRequestBody;
import com.didichuxing.doraemonkit.kit.weaknetwork.RpcSpeedLimitResponseBody;
import com.didichuxing.doraemonkit.kit.weaknetwork.WeakNetworkManager;


import java.io.IOException;

import didihttp.HttpUrl;
import didihttp.Interceptor;
import didihttp.Request;
import didihttp.RequestBody;
import didihttp.Response;
import didihttp.ResponseBody;


/**
 * 用于模拟弱网的拦截器
 * <p>
 * Created by xiandanin on 2019-05-09 16:29
 *
 * @author didi
 */
public class RpcWeakNetworkInterceptor extends AbsDoKitRpcInterceptor {
    private static final String TAG = "DoraemonWeakNetworkInterceptor";

    @Override
    public Response intercept(Chain chain) throws IOException {
        if (!WeakNetworkManager.get().isActive()) {
            Request request = chain.request();
            return chain.proceed(request);
        }
        final int type = WeakNetworkManager.get().getType();
        final HttpUrl url = chain.request().url();
        switch (type) {
            case WeakNetworkManager.TYPE_TIMEOUT:
                //超时
                return simulateTimeOut(chain);
            case WeakNetworkManager.TYPE_SPEED_LIMIT:
                //限速
                return simulateSpeedLimit(chain);
            default:
                //断网
                return simulateOffNetwork(chain);
        }
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
        SystemClock.sleep(WeakNetworkManager.get().getTimeOutMillis());
        final Response response = chain.proceed(chain.request());
        ResponseBody responseBody = ResponseBody.create(response.body().contentType(), "");
        Response newResponse = response.newBuilder()
                .code(400)
                .message(String.format("failed to connect to %s  after %dms", chain.request().url().host(), WeakNetworkManager.get().getTimeOutMillis()))
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
            final RequestBody requestBody = WeakNetworkManager.get().getRequestSpeed() > 0 ? new RpcSpeedLimitRequestBody(WeakNetworkManager.get().getRequestSpeed(), body) : body;
            request = request.newBuilder().method(request.method(), requestBody).build();
        }
        final Response response = chain.proceed(request);
        //大于0使用限速的body 否则使用原始body
        final ResponseBody responseBody = response.body();
        final ResponseBody newResponseBody = WeakNetworkManager.get().getResponseSpeed() > 0 ? new RpcSpeedLimitResponseBody(WeakNetworkManager.get().getResponseSpeed(), responseBody) : responseBody;
        return response.newBuilder().body(newResponseBody).build();
    }
}
