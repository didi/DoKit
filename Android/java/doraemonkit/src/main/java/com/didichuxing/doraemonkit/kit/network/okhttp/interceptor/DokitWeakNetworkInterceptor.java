package com.didichuxing.doraemonkit.kit.network.okhttp.interceptor;

import androidx.annotation.NonNull;

import com.didichuxing.doraemonkit.kit.weaknetwork.WeakNetworkManager;
import com.didichuxing.doraemonkit.util.LogHelper;


import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 用于模拟弱网的拦截器
 * <p>
 * Created by jint on 2019-05-09 16:29
 *
 * @author didi
 */
public class DokitWeakNetworkInterceptor extends AbsDoKitInterceptor {

    @NonNull
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
                return WeakNetworkManager.get().simulateTimeOut(chain);
            case WeakNetworkManager.TYPE_SPEED_LIMIT:
                //限速
                return WeakNetworkManager.get().simulateSpeedLimit(chain);
            default:
                //断网
                return WeakNetworkManager.get().simulateOffNetwork(chain);
        }
    }
}
