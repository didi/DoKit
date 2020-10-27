package com.didichuxing.doraemonkit.plugin

import java.io.File

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020/10/20-15:13
 * 描    述：
 * 修订历史：
 * ================================================
 */
object DoKitPluginUtil {
    const val BYTE = 1
    const val KB = 1024
    const val MB = 1048576
    const val GB = 1073741824

    fun FileSize(file: File): String? {
        if (!file.isFile) {
            return "0kb"
        }
        if (file.isDirectory) {
            return "0kb"
        }
        val fileLength = file.length()
        return byte2FitMemorySize(fileLength, 2)
    }

    fun byte2FitMemorySize(byteSize: Long, precision: Int): String? {
        require(precision >= 0) { "precision shouldn't be less than zero!" }
        return if (byteSize < 0) {
            throw IllegalArgumentException("byteSize shouldn't be less than zero!")
        } else if (byteSize < KB) {
            String.format("%." + precision + "fB", byteSize.toDouble())
        } else if (byteSize < MB) {
            String.format("%." + precision + "fKB", byteSize.toDouble() / KB)
        } else if (byteSize < GB) {
            String.format("%." + precision + "fMB", byteSize.toDouble() / MB)
        } else {
            String.format("%." + precision + "fGB", byteSize.toDouble() / GB)
        }
    }
}