
package com.didichuxing.doraemonkit.kit.network.utils

import okhttp3.Response
import okio.Buffer
import okio.GzipSource
import java.nio.charset.Charset

internal fun Response.encoding() =
    this.header("content-encoding") ?: this.header("Content-Encoding")

internal fun Response.charset(): Charset {
    this.encoding()
        ?.takeIf { Charset.isSupported(it) }
        ?.also {
            return Charset.forName(it)
        }
    return body()?.contentType()?.charset() ?: Charset.defaultCharset()
}

internal fun Response.bodyContent(): String = body()
    ?.let { body ->
        val source = body.source()
            .apply {
                request(Long.MAX_VALUE)
            }
        var buffer = source.buffer
        val encoding = encoding()
        if ("gzip".equals(encoding, true)) {
            GzipSource(buffer.clone()).use { gzippedBody ->
                buffer = Buffer().also { it.writeAll(gzippedBody) }
            }
        }
        buffer
    }
    ?.clone()
    ?.readString(charset())
    ?: ""
