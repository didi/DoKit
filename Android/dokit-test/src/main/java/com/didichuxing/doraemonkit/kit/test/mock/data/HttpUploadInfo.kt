package com.didichuxing.doraemonkit.kit.test.mock.data

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2021/6/21-17:04
 * 描    述：
 * 修订历史：
 * ================================================
 */
data class HttpUploadInfo(
    val pId: String,
    val caseId: String,
    val originKey: String,
    val key: String,
    val method: String = "GET",
    val path: String,
    val fragment: String?,
    val contentType: String?,
    val query: Map<String, String>?,
    val requestBody: Map<String, String>?,
    val responseBody: String
)
