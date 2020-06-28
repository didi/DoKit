/*
 * Copyright (c) 2014-present, Facebook, Inc.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */
package com.didichuxing.doraemonkit.kit.network.okhttp.core

import com.didichuxing.doraemonkit.kit.network.okhttp.stream.GunzippingOutputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.zip.InflaterOutputStream

/**
 * @desc: 请求body解析器
 */
class RequestBodyHelper {
    private var mDeflatedOutput: ByteArrayOutputStream? = null

    @Throws(IOException::class)
    fun createBodySink(contentEncoding: String?): OutputStream {
        val deflatingOutput: OutputStream
        val deflatedOutput = ByteArrayOutputStream()
        deflatingOutput = if (GZIP_ENCODING == contentEncoding) {
            GunzippingOutputStream.create(deflatedOutput)
        } else if (DEFLATE_ENCODING == contentEncoding) {
            InflaterOutputStream(deflatedOutput)
        } else {
            deflatedOutput
        }
        mDeflatedOutput = deflatedOutput
        return deflatingOutput
    }

    val displayBody: ByteArray
        get() {
            throwIfNoBody()
            return mDeflatedOutput!!.toByteArray()
        }

    fun hasBody(): Boolean {
        return mDeflatedOutput != null
    }

    private fun throwIfNoBody() {
        check(hasBody()) { "No body found; has createBodySink been called?" }
    }

    companion object {
        private const val GZIP_ENCODING = "gzip"
        private const val DEFLATE_ENCODING = "deflate"
    }
}