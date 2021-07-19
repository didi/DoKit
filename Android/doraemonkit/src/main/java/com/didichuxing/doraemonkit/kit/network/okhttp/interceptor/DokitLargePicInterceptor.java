package com.didichuxing.doraemonkit.kit.network.okhttp.interceptor;


import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.didichuxing.doraemonkit.aop.DokitPluginConfig;
import com.didichuxing.doraemonkit.config.PerformanceSpInfoConfig;
import com.didichuxing.doraemonkit.kit.largepicture.LargePictureManager;
import com.didichuxing.doraemonkit.kit.network.okhttp.InterceptorUtil;


import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 大图拦截器
 */
public class DokitLargePicInterceptor extends AbsDoKitInterceptor {

    @NonNull
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Response response = chain.proceed(request);
        if (!DokitPluginConfig.SWITCH_BIG_IMG) {
            return response;
        }
        String contentType = response.header("Content-Type");

        if (InterceptorUtil.isImg(contentType)) {
            if (PerformanceSpInfoConfig.isLargeImgOpen()) {
                processResponse(response);
            }
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