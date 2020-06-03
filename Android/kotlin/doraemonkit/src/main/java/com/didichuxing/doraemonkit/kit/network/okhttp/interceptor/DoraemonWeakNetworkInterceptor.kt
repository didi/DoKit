package com.didichuxing.doraemonkit.kit.network.okhttp.interceptor

import okhttp3.Interceptor
import okhttp3.Response

/**
 * 用于模拟弱网的拦截器
 *
 *
 * Created by jint on 2019-05-09 16:29
 *
 * @author didi
 */
class DoraemonWeakNetworkInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        return chain.proceed(chain.request())
    }
}