package com.didichuxing.doraemonkit.kit.network.okhttp.interceptor;



import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 大图拦截器
 */
public class LargePictureInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        return chain.proceed(request);
    }

}