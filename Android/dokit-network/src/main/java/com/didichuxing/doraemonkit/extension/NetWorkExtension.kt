package com.didichuxing.doraemonkit.extension

import com.didichuxing.doraemonkit.kit.network.room_db.DokitDbManager
import com.didichuxing.doraemonkit.util.EncodeUtils
import com.didichuxing.doraemonkit.util.GsonUtils
import okhttp3.RequestBody
import okio.Buffer
import java.util.*


/**
 * didi Create on 2022/10/31 .
 *
 * Copyright (c) 2022/10/31 by didiglobal.com.
 *
 * @author <a href="realonlyone@126.com">zhangjun</a>
 * @version 1.0
 * @Date 2022/10/31 2:05 下午
 * @Description 用一句话说明文件功能
 */




/**
 * queryBody转成Map
 */

fun RequestBody?.toMap(): MutableMap<String, String> {
    var map = mutableMapOf<String, String>()
    if (this == null || this.contentType() == null) {
        return map
    }

    val buffer = Buffer()
    this.writeTo(buffer)
    val strBody = EncodeUtils.urlDecode(buffer.readUtf8())
    strBody?.let {
        val contentType = this.contentType().toString().toLowerCase(Locale.ROOT)
        when {
            contentType.contains(DokitDbManager.MEDIA_TYPE_FORM) -> {
                return strBody.toMap()
            }
            contentType.contains(DokitDbManager.MEDIA_TYPE_JSON) -> {
                try {
                    map = GsonUtils.fromJson<MutableMap<String, String>>(strBody, MutableMap::class.java)
                } catch (e: Exception) {
                    map["json"] = strBody
                }
                return map
            }
            contentType.contains(DokitDbManager.MEDIA_TYPE_PLAIN) -> {
                map["plain"] = strBody
                return map
            }
            else -> {
                map["other"] = strBody
                return map
            }
        }
    }

    return map
}
