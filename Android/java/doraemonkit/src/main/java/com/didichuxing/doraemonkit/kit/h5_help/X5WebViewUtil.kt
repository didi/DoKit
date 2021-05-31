package com.didichuxing.doraemonkit.kit.h5_help

import com.didichuxing.doraemonkit.util.ReflectUtils

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020/11/3-20:02
 * 描    述：
 * 修订历史：
 * ================================================
 */
object X5WebViewUtil {

    /**
     * 项目中是否引入了X5WebView
     */
    fun hasImpX5WebViewLib(): Boolean {
        try {

            val schemeTel = ReflectUtils.reflect("com.tencent.smtt.sdk.WebView").field("SCHEME_TEL")
                .get<String>()
            return true
        } catch (e: Exception) {
            return false
        }
    }
}