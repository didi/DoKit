package com.didichuxing.doraemonkit.kit.network.okhttp;


import android.text.TextUtils;

import com.didichuxing.doraemonkit.kit.largepicture.LargePictureManager;
import com.didichuxing.doraemonkit.kit.network.core.ResourceType;
import com.didichuxing.doraemonkit.kit.network.core.ResourceTypeHelper;
import com.didichuxing.doraemonkit.kit.network.httpurlconnection.interceptor.HttpResponse;
import com.didichuxing.doraemonkit.util.LogHelper;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 大图拦截器
 */
public class LargePictureInterceptor implements Interceptor {
    public static final String TAG = "LargePictureInterceptor";

    private ResourceTypeHelper mResourceTypeHelper;

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Response response = chain.proceed(request);
        String contentType = response.header("Content-Type");
        ResourceType resourceType =
                contentType != null ?
                        getResourceTypeHelper().determineResourceType(contentType) :
                        null;
        if (resourceType == ResourceType.IMAGE) {
            processResponse(response);
        }
        LogHelper.i(TAG, "=====contentType=====" + contentType);
        return response;
    }

    private ResourceTypeHelper getResourceTypeHelper() {
        if (mResourceTypeHelper == null) {
            mResourceTypeHelper = new ResourceTypeHelper();
        }
        return mResourceTypeHelper;
    }

    private void processResponse(Response response) {
        String field = response.header("Content-Length");
        LogHelper.i(TAG, "img url===>" + response.request().url().toString() + " fieldSize===>" + field);
        if (!TextUtils.isEmpty(field)) {
            LargePictureManager.getInstance().process(response.request().url().toString(), Integer.parseInt(field));
        }
    }
}