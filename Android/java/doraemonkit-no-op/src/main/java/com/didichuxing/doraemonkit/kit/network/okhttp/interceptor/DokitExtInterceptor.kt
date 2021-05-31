package com.didichuxing.doraemonkit.kit.network.okhttp.interceptor

import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

/**
 * Author: xuweiyu
 * Date: 5/11/21
 * Email: wizz.xu@outlook.com
 * Description: Dokit 扩展网络拦截器
 */
class DokitExtInterceptor : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        return chain.proceed(chain.request())
    }

    interface DokitExtInterceptorProxy {
        fun intercept(chain: Interceptor.Chain): Response
    }

    companion object {
        const val TAG = "DokitExtInterceptor"
        var dokitExtInterceptorProxy: DokitExtInterceptorProxy? = null
    }
}