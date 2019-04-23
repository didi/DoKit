package com.didichuxing.doraemonkit.kit.network.httpurlconnection.interceptor;

import java.io.IOException;
import java.util.List;

/**
 * 请求body处理链
 */
public class HttpRequestStreamChain extends InterceptorChain<HttpRequest, DKInterceptor> {


    @Override
    protected void processNext(HttpRequest source, List<DKInterceptor> interceptors, int index) throws IOException {
        DKInterceptor interceptor = interceptors.get(index);
        if (interceptor != null) {
            index++;
            interceptor.intercept(this, source);
        }
    }

    public HttpRequestStreamChain(List<DKInterceptor> interceptors) {
        this(interceptors, 0);
    }

    public HttpRequestStreamChain(List<DKInterceptor> interceptors,
                                  int index) {
        super(interceptors, index);
    }

}
