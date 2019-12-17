package com.didichuxing.doraemonkit.kit.network.okhttp.interceptor;

import com.didichuxing.doraemonkit.kit.weaknetwork.WeakNetworkManager;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 用于模拟弱网的拦截器
 *
 * @author denghaha
 * created 2019-05-09 16:29
 */
public class DoraemonWeakNetworkInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        if (!WeakNetworkManager.get().isActive()) {
            Request request = chain.request();
            return chain.proceed(request);
        }
        final int type = WeakNetworkManager.get().getType();
        switch (type) {
            case WeakNetworkManager.TYPE_TIMEOUT:
                //超时
                final HttpUrl url = chain.request().url();
                throw WeakNetworkManager.get().simulateTimeOut(url.host(), url.port());
            case WeakNetworkManager.TYPE_SPEED_LIMIT:
                //限速
                return WeakNetworkManager.get().simulateSpeedLimit(chain);
            default:
                //断网
                throw WeakNetworkManager.get().simulateOffNetwork(chain.request().url().host());
        }
    }
}
