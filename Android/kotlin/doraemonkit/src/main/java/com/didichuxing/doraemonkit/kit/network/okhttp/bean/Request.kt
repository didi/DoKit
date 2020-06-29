package com.didichuxing.doraemonkit.kit.network.okhttp.bean

import android.text.TextUtils
import java.io.Serializable

/**
 * 请求bean
 */
class Request : Serializable {
    var url: String? = null
    var method: String? = null
    var headers: String? = null
    var postData: String? = null
    var encode: String? = null
    override fun toString(): String {
        return String.format("[%s %s %s %s]", url, method, headers.toString(), postData)
    }

    fun filter(text: String?): Boolean {
        return !TextUtils.isEmpty(url) && url!!.contains(text!!)
    }
}