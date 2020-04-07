package com.didichuxing.doraemonkit.kit.network.httpurlconnection.interceptor;

import androidx.annotation.NonNull;
import android.text.TextUtils;

import com.didichuxing.doraemonkit.kit.largepicture.LargePictureManager;
import com.didichuxing.doraemonkit.kit.network.core.ResourceType;
import com.didichuxing.doraemonkit.kit.network.core.ResourceTypeHelper;
import com.didichuxing.doraemonkit.kit.network.httpurlconnection.HttpRequest;
import com.didichuxing.doraemonkit.kit.network.httpurlconnection.HttpResponse;
import com.didichuxing.doraemonkit.kit.network.httpurlconnection.chain.HttpRequestChain;
import com.didichuxing.doraemonkit.kit.network.httpurlconnection.chain.HttpRequestStreamChain;
import com.didichuxing.doraemonkit.kit.network.httpurlconnection.chain.HttpResponseChain;
import com.didichuxing.doraemonkit.kit.network.httpurlconnection.chain.HttpResponseStreamChain;

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
        if (!TextUtils.isEmpty(field)) {
            LargePictureManager.getInstance().process(response.getUrl(), Integer.parseInt(field));
        }

    }

}
