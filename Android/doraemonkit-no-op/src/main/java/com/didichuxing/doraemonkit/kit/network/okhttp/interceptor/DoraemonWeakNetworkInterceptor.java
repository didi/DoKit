package com.didichuxing.doraemonkit.kit.network.okhttp.interceptor;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 用于模拟弱网的拦截器
 * <p>
 * Created by xiandanin on 2019-05-09 16:29
 */
public class DoraemonWeakNetworkInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        return chain.proceed(request);
    }
}
