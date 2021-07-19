package com.didichuxing.doraemonkit

import com.didichuxing.doraemonkit.kit.network.room_db.DokitDbManager
import com.didichuxing.doraemonkit.util.EncodeUtils
import didihttp.RequestBody
import okio.Buffer
import java.util.*

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2021/2/8-10:42
 * 描    述：
 * 修订历史：
 * ================================================
 */


/**
 * a=aa&b=bb&c=cc
 * query键值对字符串转成Map
 */
fun String.toMap(): MutableMap<String, String> {

    val map = mutableMapOf<String, String>()

    if (this.isBlank()) {
        return map
    }
    val params = this.split("&")
    params.forEach { kv ->
        val kvs = kv.split("=")
        if (kvs.size == 2) {
            map[kvs[0]] = kvs[1]
        }
    }
    return map
}

/**
 * queryBody转成Map
 */
fun RequestBody?.toMap(): MutableMap<String, String> {
    val map = mutableMapOf<String, String>()
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
                map["json"] = strBody
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


/**
 * 对map 针对key进行排序
 */
fun MutableMap<String, String>.sortedByKey(): Map<String, String> {
    return this.toList().sortedBy { (key, _) -> key }.toMap()
}

