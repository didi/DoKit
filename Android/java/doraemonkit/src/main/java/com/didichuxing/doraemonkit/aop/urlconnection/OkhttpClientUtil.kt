package com.didichuxing.doraemonkit.aop.urlconnection

import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020/6/17-10:25
 * 描    述：将urlconnection 代理成okhttpClient
 * 修订历史：
 * ================================================
 */
object OkhttpClientUtil {
    private val DEFAULT_MILLISECONDS: Long = 60000
    val okhttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .retryOnConnectionFailure(true)
            .readTimeout(DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS)
            .writeTimeout(DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS)
            .connectTimeout(DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS)
            .build()
    }
}