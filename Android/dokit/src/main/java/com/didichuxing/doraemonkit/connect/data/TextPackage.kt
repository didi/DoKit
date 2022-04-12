package com.didichuxing.doraemonkit.connect.data


/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020/11/17-17:11
 * 描    述：包结构
 * 修订历史：
 * ================================================
 */
data class TextPackage(
    val pid: String,
    val type: PackageType,
    val data: String,
    val channelSerial: String = "android",
    var connectSerial: String = "",
    var contentType: String = "text",
    var message: String = "",
    var code: Int = 0
)


