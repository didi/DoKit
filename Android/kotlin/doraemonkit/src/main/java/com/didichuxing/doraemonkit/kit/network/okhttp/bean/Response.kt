package com.didichuxing.doraemonkit.kit.network.okhttp.bean

import java.io.Serializable

/**
 * 响应bean,不包含内容Body
 */
class Response : Serializable {
    var url: String? = null
    var status = 0
    var headers: String? = null
    var mimeType: String? = null
    override fun toString(): String {
        return String.format("[%s %d  %s %s]", url, status, headers.toString(), mimeType)
    }
}