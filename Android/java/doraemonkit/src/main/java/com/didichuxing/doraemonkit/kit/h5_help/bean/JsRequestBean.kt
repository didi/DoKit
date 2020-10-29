package com.didichuxing.doraemonkit.kit.h5_help.bean

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020/8/28-15:46
 * 描    述：js request bean
 * 修订历史：
 * ================================================
 */
data class JsRequestBean(
    var requestId: String?,
    var url: String?,
    var method: String?,
    var headers: MutableMap<String?, String?>?,
    var mimeType: String?,
    var body: String?
)