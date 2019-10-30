package com.didichuxing.doraemonkit.kit.network.httpurlconnection;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.didichuxing.doraemonkit.kit.largepicture.LargePictureManager;
import com.didichuxing.doraemonkit.kit.network.core.ResourceType;
import com.didichuxing.doraemonkit.kit.network.core.ResourceTypeHelper;
import com.didichuxing.doraemonkit.kit.network.httpurlconnection.interceptor.DKInterceptor;
import com.didichuxing.doraemonkit.kit.network.httpurlconnection.interceptor.HttpRequest;
import com.didichuxing.doraemonkit.kit.network.httpurlconnection.interceptor.HttpRequestChain;
import com.didichuxing.doraemonkit.kit.network.httpurlconnection.interceptor.HttpRequestStreamChain;
import com.didichuxing.doraemonkit.kit.network.httpurlconnection.interceptor.HttpResponse;
import com.didichuxing.doraemonkit.kit.network.httpurlconnection.interceptor.HttpResponseChain;
import com.didichuxing.doraemonkit.kit.network.httpurlconnection.interceptor.HttpResponseStreamChain;
import com.didichuxing.doraemonkit.util.LogHelper;

import java.io.IOException;

/**
 * @desc: 大图拦截器
 */
public class LargePictureInterceptor implements DKInterceptor<HttpRequest, HttpResponse> {
    public static final String TAG = "LargePictureInterceptor";
    public LargePictureInterceptor() {
    }

    private ResourceTypeHelper mResourceTypeHelper;

    @Override
    public void intercept(@NonNull HttpRequestChain chain, @NonNull HttpRequest request) throws IOException {
        chain.process(request);
    }

    @Override
    public void intercept(@NonNull HttpResponseChain chain, @NonNull HttpResponse response) throws IOException {
        String contentType = response.getHeaderField("Content-Type");
        ResourceType resourceType =
                contentType != null ?
                        getResourceTypeHelper().determineResourceType(contentType) :
                        null;
        if (resourceType == ResourceType.IMAGE) {
            processResponse(response);
        }
        chain.process(response);
    }

    @Override
    public void intercept(@NonNull HttpRequestStreamChain chain, @NonNull HttpRequest request) throws IOException {
        chain.process(request);
    }

    @Override
    public void intercept(@NonNull HttpResponseStreamChain chain, @NonNull HttpResponse response) throws IOException {
        chain.process(response);
    }

    private ResourceTypeHelper getResourceTypeHelper() {
        if (mResourceTypeHelper == null) {
            mResourceTypeHelper = new ResourceTypeHelper();
        }
        return mResourceTypeHelper;
    }

    private void processResponse(HttpResponse response) {
        String field = response.getHeaderField("Content-Length");
        LogHelper.i(TAG,"img url===>" + response.getUrl() + " fieldSize===>" + field);
        if (!TextUtils.isEmpty(field)) {
            LargePictureManager.getInstance().process(response.getUrl(), Integer.parseInt(field));
        }

    }

}
