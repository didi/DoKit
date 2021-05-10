package com.didichuxing.doraemonkit.kit.network.okhttp.interceptor;


import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @author jintai
 * @desc: 接口mock拦截器
 */
public class DokitMockInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        return chain.proceed(request);
    }

}