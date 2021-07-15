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

    fun fileSize(file: File, precision: Int): String? {
        if (!file.isFile) {
            return "0kb"
        }
        if (file.isDirectory) {
            return "0kb"
        }
        val fileLength = file.length()
        return byte2FitMemorySize(fileLength, precision)
    }

    private fun byte2FitMemorySize(byteSize: Long, precision: Int): String? {
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

    private fun getNextChunk(version: String, n: Int, p: Int): Pair<Int, Int> {
        // if pointer is set to the end of string
        // return 0
        if (p > n - 1) {
            return Pair(0, p)
        }
        // find the end of chunk
        var i = 0
        var pEnd = p
        while (pEnd < n && version[pEnd].equals(".")) {
            ++pEnd
        }
        // retrieve the chunk
        i = if (pEnd != n - 1) {
            version.substring(p, pEnd).toInt()
        } else {
            version.substring(p, n).toInt()
        }
        // find the beginning of next chunk
        val q = pEnd + 1

        return Pair(i, q)

    }

    /**
     * 比较version的大小
     */
    fun compareVersion(version1: String, version2: String): Int {
        var p1 = 0
        var p2 = 0
        val n1 = version1.length
        val n2 = version2.length
        var i1: Int
        var i2: Int
        var pair: Pair<Int, Int>
        while (p1 < n1 || p2 < n2) {
            pair = getNextChunk(version1, n1, p1)
            i1 = pair.first
            p1 = pair.second

            pair = getNextChunk(version2, n2, p2)
            i2 = pair.first
            p2 = pair.second
            if (i1 != i2) {
                return if (i1 > i2) {
                    1
                } else {
                    -1
                }
            }
        }
        return 0
    }
}