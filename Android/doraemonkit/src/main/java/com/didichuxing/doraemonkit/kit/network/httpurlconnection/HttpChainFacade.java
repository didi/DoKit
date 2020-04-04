package com.didichuxing.doraemonkit.kit.network.httpurlconnection;

import com.didichuxing.doraemonkit.kit.network.httpurlconnection.chain.HttpRequestChain;
import com.didichuxing.doraemonkit.kit.network.httpurlconnection.chain.HttpRequestStreamChain;
import com.didichuxing.doraemonkit.kit.network.httpurlconnection.chain.HttpResponseChain;
import com.didichuxing.doraemonkit.kit.network.httpurlconnection.chain.HttpResponseStreamChain;
import com.didichuxing.doraemonkit.kit.network.httpurlconnection.interceptor.DKInterceptor;

import java.io.IOException;
import java.util.List;

/**
 *  2019/3/14
 * @desc: 对几个处理链的包装
 */
public class HttpChainFacade {
    private final HttpRequestChain mHttpRequestChain;
    private final HttpResponseChain mHttpResponseChain;
    private final HttpRequestStreamChain mHttpRequestStreamChain;
    private final HttpResponseStreamChain mHttpResponseStreamChain;

    public HttpChainFacade(List<DKInterceptor> interceptors) {
        mHttpRequestChain = new HttpRequestChain(interceptors);
        mHttpResponseChain = new HttpResponseChain(interceptors);
        mHttpRequestStreamChain = new HttpRequestStreamChain(interceptors);
        mHttpResponseStreamChain = new HttpResponseStreamChain(interceptors);
    }

    public void process(HttpRequest request) throws IOException {
        mHttpRequestChain.process(request);
    }

    public void process(HttpResponse response) throws IOException {
        mHttpResponseChain.process(response);
    }

    public void processStream(HttpRequest request) throws IOException {
        mHttpRequestStreamChain.process(request);
    }

    public void processStream(HttpResponse response) throws IOException {
        mHttpResponseStreamChain.process(response);
    }

}
