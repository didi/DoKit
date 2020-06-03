package com.didichuxing.doraemonkit.kit.network.okhttp.interceptor

import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

/**
 * 用于模拟弱网的拦截器
 *
 *
 * Created by xiandanin on 2019-05-09 16:29
 */
class DoraemonWeakNetworkInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        return chain.proceed(request)
    }
}