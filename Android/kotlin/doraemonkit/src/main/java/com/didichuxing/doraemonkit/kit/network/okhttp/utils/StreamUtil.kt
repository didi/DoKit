/*
 * Copyright (c) 2014-present, Facebook, Inc.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */
package com.didichuxing.doraemonkit.kit.network.okhttp.utils

import android.util.Pair
import java.io.Closeable
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.*

object StreamUtil {
    @JvmStatic
    @Throws(IOException::class)
    fun copy(input: InputStream, output: OutputStream, buffer: ByteArray?) {
        var n: Int
        while (input.read(buffer).also { n = it } != -1) {
            output.write(buffer, 0, n)
        }
    }

    @Throws(IOException::class)
    fun close(closeable: Closeable?, hideException: Boolean) {
        if (closeable != null) {
            if (hideException) {
                try {
                    closeable.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            } else {
                closeable.close()
            }
        }
    }

    fun convertHeaders(map: Map<String?, List<String>>?): ArrayList<Pair<String?, String>> {
        val array = ArrayList<Pair<String?, String>>()
        if (map == null) {
            return array
        }
        for ((key, value) in map) {
            for (mapEntryValue in value) {
                // HttpURLConnection puts a weird null entry in the header map that corresponds to
                // the HTTP response line (for instance, HTTP/1.1 200 OK).  Ignore that weirdness...
                if (key != null) {
                    array.add(Pair.create(key, mapEntryValue))
                }
            }
        }
        return array
    }
}