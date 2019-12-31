package com.didichuxing.doraemonkit.kit.network.httpurlconnection.interceptor;

import android.support.annotation.NonNull;

import com.didichuxing.doraemonkit.kit.network.core.ResourceTypeHelper;
import com.didichuxing.doraemonkit.kit.network.httpurlconnection.HttpRequest;
import com.didichuxing.doraemonkit.kit.network.httpurlconnection.HttpResponse;
import com.didichuxing.doraemonkit.kit.network.httpurlconnection.chain.HttpRequestChain;
import com.didichuxing.doraemonkit.kit.network.httpurlconnection.chain.HttpRequestStreamChain;
import com.didichuxing.doraemonkit.kit.network.httpurlconnection.chain.HttpResponseChain;
import com.didichuxing.doraemonkit.kit.network.httpurlconnection.chain.HttpResponseStreamChain;
import com.didichuxing.doraemonkit.okgo.OkGo;

import java.io.IOException;

import okhttp3.HttpUrl;

/**
 * @author jintai
 * @desc: 接口mock拦截器
 */
public class MockInterceptor implements DKInterceptor<HttpRequest, HttpResponse> {
    public static final String TAG = "MockInterceptor";


    public MockInterceptor() {
    }

    private ResourceTypeHelper mResourceTypeHelper;

    @Override
    public void intercept(@NonNull HttpRequestChain chain, @NonNull HttpRequest oldRequest) throws IOException {
        chain.process(oldRequest);
    }

    /**
     * 命中拦截
     *
     * @param chain
     * @param response
     * @throws IOException
     */
    @Override
    public void intercept(@NonNull HttpResponseChain chain, @NonNull HttpResponse response) throws IOException {
        try {
            httpProxy(response);
        } catch (Exception e) {
            e.printStackTrace();
        }
        chain.process(response);
    }

    @Override
    public void intercept(@NonNull HttpRequestStreamChain chain, @NonNull HttpRequest request) throws IOException {
        chain.process(request);
    }

    @Override
    public void intercept(@NonNull HttpResponseStreamChain chain, @NonNull HttpResponse response) throws IOException {
        try {
            httpProxy(response);
        } catch (Exception e) {
            e.printStackTrace();
        }
        chain.process(response);
    }


    /**
     * 将HttpUrlConnection请求代理成OkHttp发送
     *
     * @param httpResponse
     * @throws Exception
     */
    private void httpProxy(HttpResponse httpResponse) throws Exception {

        HttpUrl mockUrl = HttpUrl.parse(httpResponse.getUrl());
        if (mockUrl != null) {
            HttpUrl originUrl = HttpUrl.parse(mockUrl.queryParameter("originUrl"));
            if (originUrl != null) {
                //用okhttp代理发送
                OkGo.<String>get(originUrl.toString()).execute();
            }
        }

    }


}
