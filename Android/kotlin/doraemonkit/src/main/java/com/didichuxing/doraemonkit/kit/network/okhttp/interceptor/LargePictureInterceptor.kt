package com.didichuxing.doraemonkit.kit.network.okhttp.interceptor

import android.text.TextUtils
import com.didichuxing.doraemonkit.aop.DokitPluginConfig
import com.didichuxing.doraemonkit.config.PerformanceSpInfoConfig.isLargeImgOpen
import com.didichuxing.doraemonkit.kit.largepicture.LargePictureManager
import com.didichuxing.doraemonkit.kit.network.okhttp.InterceptorUtil.isImg
import okhttp3.Interceptor
import okhttp3.Response


/**
 * 大图拦截器
 */
class LargePictureInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)
        if (!DokitPluginConfig.SWITCH_BIG_IMG) {
            return response
        }
        val contentType = response.header("Content-Type")

        if (isImg(contentType)) {
            if (isLargeImgOpen()) {
                processResponse(response)
            }
        }
        return response
    }
    private fun processResponse(response: Response) {
        val field = response.header("Content-Length")
        if (!TextUtils.isEmpty(field)) {
            LargePictureManager.getInstance().process(response.request().url().toString(), field!!.toInt())
        }
    }

}