package com.didichuxing.doraemonkit.kit.filemanager.action

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020/6/23-16:38
 * 描    述：
 * 修订历史：
 * ================================================
 */
object RequestErrorAction {
    fun createErrorInfo(error: String): MutableMap<String, Any> {
        return mutableMapOf<String, Any>().apply {
            this["code"] = 400
            this["data"] = error
        }
    }
}