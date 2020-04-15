package com.didichuxing.doraemonkit.kit.network.okhttp.interceptor;


import android.text.TextUtils;

import com.didichuxing.doraemonkit.kit.largepicture.LargePictureManager;
import com.didichuxing.doraemonkit.kit.network.okhttp.InterceptorUtil;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 大图拦截器
 */
public class LargePictureInterceptor implements Interceptor {
    public static final String TAG = "LargePictureInterceptor";


    @NotNull
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Response response = chain.proceed(request);
        String contentType = response.header("Content-Type");

        if (InterceptorUtil.isImg(contentType)) {
            processResponse(response);
        }
        return response;
    }


    private void processResponse(Response response) {
        String field = response.header("Content-Length");
        if (!TextUtils.isEmpty(field)) {
            LargePictureManager.getInstance().process(response.request().url().toString(), Integer.parseInt(field));
        }
    }
}