package com.didichuxing.doraemonkit.kit.network.okhttp;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @author denghaha
 * created 2019-05-10 11:56
 */
public class DoraemonWeakNetworkInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        return chain.proceed(request);
    }


}