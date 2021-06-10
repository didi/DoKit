package com.didichuxing.doraemondemo

import com.didichuxing.doraemonkit.util.LogHelper
import okhttp3.Interceptor
import okhttp3.Response

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：4/22/21-11:21
 * 描    述：
 * 修订历史：
 * ================================================
 */
class CustomInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        LogHelper.i("CustomInterceptor", "===custom intercept===")
        return chain.proceed(chain.request())
    }
}