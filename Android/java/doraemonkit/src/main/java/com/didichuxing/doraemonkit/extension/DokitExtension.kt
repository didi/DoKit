package com.didichuxing.doraemonkit.extension

import com.didichuxing.doraemonkit.aop.DokitThirdLibInfo
import com.didichuxing.doraemonkit.util.LogHelper

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
 * 命名函数
 */
val err = fun(a: String, b: String) {

}


/**
 * Boolean 扩展函数
 */
fun Boolean?.isTrue(
    error: (String) -> Unit = { LogHelper.e("DoKit", it) },
    action: () -> Unit
) {
    if (this == null) {
        error("Boolean is null")
    }
    if (this == true) {
        action()
    }
}


/**
 * Boolean 扩展函数
 */
fun Boolean?.isFalse(
    error: (String) -> Unit = { LogHelper.e("DoKit", it) },
    action: () -> Unit
) {
    if (this == null) {
        error("Boolean is null")
    }
    if (this == false) {
        action()
    }
}

/**
 * 查找三方库时候存在
 */
fun hasThirdLib(groupId: String, artifactId: String): Boolean {
    return try {
        val value = DokitThirdLibInfo.THIRD_LIB_INFOS_SIMPLE["${groupId}:${artifactId}"]
        value != null
    } catch (e: Exception) {
        false
    }
}