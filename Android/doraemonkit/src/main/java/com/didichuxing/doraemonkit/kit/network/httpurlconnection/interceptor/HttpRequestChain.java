package com.didichuxing.doraemonkit.kit.network.httpurlconnection.interceptor;

import java.io.IOException;
import java.util.List;

/**
 * 请求处理链
 */
public class HttpRequestChain extends InterceptorChain<HttpRequest, DKInterceptor> {


    @Override
    protected void processNext(HttpRequest source, List<DKInterceptor> interceptors, int index) throws IOException {
        DKInterceptor interceptor = interceptors.get(index);
        if (interceptor != null) {
            index++;
            interceptor.intercept(this, source);
        }
    }

    public HttpRequestChain(List<DKInterceptor> interceptors) {
        this(interceptors, 0);
    }

    public HttpRequestChain(List<DKInterceptor> interceptors,
                            int index) {
        super(interceptors, index);
    }

}
