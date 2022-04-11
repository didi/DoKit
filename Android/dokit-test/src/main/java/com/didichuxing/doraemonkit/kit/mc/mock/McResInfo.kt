package com.didichuxing.doraemonkit.kit.mc.mock

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2021/7/1-19:15
 * 描    述：
 * 修订历史：
 * ================================================
 */
data class McResInfo<T>(
    val code: Int = 0,
    val msg: String = "",
    var data: T? = null
)
