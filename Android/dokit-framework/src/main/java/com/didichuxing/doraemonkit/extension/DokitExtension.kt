package com.didichuxing.doraemonkit.extension

import android.app.Activity
import android.util.Log
import com.didichuxing.doraemonkit.kit.core.AbsDoKitView
import com.didichuxing.doraemonkit.util.EncodeUtils
import com.didichuxing.doraemonkit.util.GsonUtils
import com.didichuxing.doraemonkit.util.LogHelper
import kotlinx.coroutines.*
import okhttp3.RequestBody
import okio.Buffer
import java.util.*
import kotlin.reflect.KClass

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2021/2/8-10:42
 * 描    述：
 * 修订历史：
 * ================================================
 */


val doKitGlobalScope: CoroutineScope by lazy {
    MainScope() + CoroutineName("DoKit")
}

val doKitGlobalExceptionHandler: CoroutineExceptionHandler by lazy {
    CoroutineExceptionHandler { coroutineContext, throwable ->
        Log.e("DoKit", "${coroutineContext[CoroutineName]} ：$throwable")
    }
}


val Activity.tagName: String
    get() {
        return this.javaClass.canonicalName ?: ""
    }

val AbsDoKitView.tagName: String
    get() {
        return this.javaClass.canonicalName ?: ""
    }

val KClass<out Any>.tagName: String
    get() {
        return this.java.canonicalName ?: ""
    }

val Class<out Any>.tagName: String
    get() {
        return this.canonicalName ?: ""
    }


/**
 * Boolean 扩展函数
 */
fun Boolean?.isTrue(
    error: (String) -> Unit = { LogHelper.e("DoKit", it) },
    isFalse: () -> Unit = {},
    action: () -> Unit
) {
    if (this == null) {
        error("Boolean is null")
    }
    if (this == true) {
        action()
    } else {
        isFalse()
    }
}

suspend fun Boolean?.isTrueWithCor(
    error: suspend (String) -> Unit = { LogHelper.e("DoKit", it) },
    isFalse: suspend () -> Unit = {},
    action: suspend () -> Unit
) {
    if (this == null) {
        error("Boolean is null")
    }
    if (this == true) {
        action()
    } else {
        isFalse()
    }
}


/**
 * Boolean 扩展函数
 */
fun Boolean?.isFalse(
    error: (String) -> Unit = { LogHelper.e("DoKit", it) },
    isTrue: () -> Unit = {},
    action: () -> Unit
) {
    if (this == null) {
        error("Boolean is null")
    }
    if (this == false) {
        action()
    } else {
        isTrue()
    }
}

/**
 * Boolean 扩展函数
 */
suspend fun Boolean?.isFalseWithCor(
    error: suspend (String) -> Unit = { LogHelper.e("DoKit", it) },
    isTrue: suspend () -> Unit = {},
    action: suspend () -> Unit
) {
    if (this == null) {
        error("Boolean is null")
    }
    if (this == false) {
        action()
    } else {
        isTrue()
    }
}

///**
// * 查找三方库时候存在
// */
//fun hasThirdLib(groupId: String, artifactId: String): Boolean {
//    return try {
//        val value = DokitThirdLibInfo.THIRD_LIB_INFOS_SIMPLE["${groupId}:${artifactId}"]
//        value != null
//    } catch (e: Exception) {
//        false
//    }
//}

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
 * RequestBody 转为字符串
 */
fun RequestBody?.string(): String {
    if (this != null && this.contentLength() > 0) {
        val buffer = Buffer()
        this.writeTo(buffer)
        val stringBody = EncodeUtils.urlDecode(buffer.readUtf8())
        return stringBody
    }
    return ""
}

/**
 * 对map 针对key进行排序
 */
fun MutableMap<String, String>.sortedByKey(): Map<String, String> {
    return this.toList().sortedBy { (key, _) -> key }.toMap()
}

