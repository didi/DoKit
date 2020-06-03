package com.didichuxing.doraemonkit.kit.network.okhttp.interceptor

import okhttp3.Interceptor
import okhttp3.Response


/**
 * 抓包拦截器
 */
class DoraemonInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        return chain.proceed(chain.request())
    }


}