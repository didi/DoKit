package com.didichuxing.doraemonkit.kit.network.okhttp.bean

import android.text.TextUtils
import java.io.Serializable

/**
 * @desc: 一条网络请求记录
 */
class NetworkRecord : Serializable {
    var mRequestId = 0
    var mRequest: Request? = null
    var mResponse: Response? = null
    var mResponseBody: String? = null
    @JvmField
    var requestLength: Long = 0
    @JvmField
    var responseLength: Long = 0
    var startTime: Long = 0
    var endTime: Long = 0
    fun filter(text: String?): Boolean {
        // 目前只支持url筛选，后续需要再扩展
        return if (mRequest != null && mRequest!!.filter(text)) {
            true
        } else false
    }

    val isGetRecord: Boolean
        get() = mRequest != null && mRequest!!.method != null && TextUtils.equals(METHOD_GET, mRequest!!.method!!.toLowerCase())

    val isPostRecord: Boolean
        get() = mRequest != null && mRequest!!.method != null && TextUtils.equals(METHOD_POST, mRequest!!.method!!.toLowerCase())

    companion object {
        private const val METHOD_GET = "get"
        private const val METHOD_POST = "post"
    }
}