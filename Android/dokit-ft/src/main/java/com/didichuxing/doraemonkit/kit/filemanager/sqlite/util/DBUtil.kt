package com.didichuxing.doraemonkit.kit.filemanager.sqlite.util

import kotlin.experimental.and

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020/6/28-19:56
 * 描    述：
 * 修订历史：
 * ================================================
 */
object DBUtil {
    private const val MAX_BLOB_LENGTH = 512

    fun blob2String(blob: ByteArray): String {
        if (blob.size <= MAX_BLOB_LENGTH) {
            try {

                return String(blob, Charsets.US_ASCII)
            } catch (e: Exception) {
                return "{blob}"
            }
        } else {
            return "{blob}"
        }
    }


}