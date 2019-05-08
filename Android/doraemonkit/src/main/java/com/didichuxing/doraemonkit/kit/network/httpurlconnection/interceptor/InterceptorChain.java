
package com.didichuxing.doraemonkit.kit.network.httpurlconnection.interceptor;

import java.io.IOException;
import java.util.List;

public abstract class InterceptorChain<T, I extends DKInterceptor> {
    protected List<I> mInterceptors;
    protected int mIndex;

    protected abstract void processNext(T buffer, List<I> interceptors, int index)
            throws IOException;

    public InterceptorChain(List<I> interceptors) {
        this(interceptors, 0);
    }

    public InterceptorChain(List<I> interceptors, int index) {
        this.mInterceptors = interceptors;
        this.mIndex = index;
    }

    public void processFinal(T source) throws IOException {
//        mSource.process(buffer);
    }

    public void process(T source) throws IOException {
        if (mIndex >= mInterceptors.size()) {
            processFinal(source);
        } else {
            processNext(source, mInterceptors, mIndex++);
        }
    }

}
