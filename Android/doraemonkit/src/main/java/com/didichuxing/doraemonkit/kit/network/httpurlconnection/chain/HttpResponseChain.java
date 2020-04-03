package com.didichuxing.doraemonkit.kit.network.httpurlconnection.chain;

import com.didichuxing.doraemonkit.kit.network.httpurlconnection.interceptor.DKInterceptor;
import com.didichuxing.doraemonkit.kit.network.httpurlconnection.HttpResponse;

import java.io.IOException;
import java.util.List;

/**
 *  2019/3/11
 * @desc: 响应处理链，不包含响应body
 */
public class HttpResponseChain extends InterceptorChain<HttpResponse, DKInterceptor> {

    public HttpResponseChain(List<DKInterceptor> interceptors) {
        super(interceptors);
    }

    public HttpResponseChain(List<DKInterceptor> interceptors, int index) {
        super(interceptors, index);
    }

    @Override
    protected void processNext(HttpResponse response, List<DKInterceptor> interceptors,
                               int index) throws IOException {
        DKInterceptor interceptor = interceptors.get(index);
        if (interceptor != null) {
            index++;
            interceptor.intercept(this, response);
        }
    }

}
