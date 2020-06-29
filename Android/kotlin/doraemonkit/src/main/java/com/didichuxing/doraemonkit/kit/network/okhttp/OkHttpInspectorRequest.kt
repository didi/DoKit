package com.didichuxing.doraemonkit.kit.network.okhttp

import com.didichuxing.doraemonkit.kit.network.okhttp.core.NetworkInterpreter.InspectorRequest
import com.didichuxing.doraemonkit.kit.network.okhttp.core.RequestBodyHelper
import okhttp3.Request
import okio.Okio
import java.io.IOException

class OkHttpInspectorRequest(
        private val mRequestId: Int,
        private val mRequest: Request,
        private val mRequestBodyHelper: RequestBodyHelper) : InspectorRequest {
    override fun id(): Int {
        return mRequestId
    }

    override fun url(): String? {
        return mRequest.url().toString()
    }

    override fun method(): String? {
        return mRequest.method()
    }

    @Throws(IOException::class)
    override fun body(): ByteArray? {
        val body = mRequest.body() ?: return null
        val out = mRequestBodyHelper.createBodySink(firstHeaderValue("Content-Encoding"))
        val bufferedSink = Okio.buffer(Okio.sink(out))
        try {
            body.writeTo(bufferedSink)
        } finally {
            bufferedSink.close()
        }
        return mRequestBodyHelper.displayBody
    }

    override fun headerCount(): Int {
        return mRequest.headers().size()
    }

    override fun headerName(index: Int): String {
        return mRequest.headers().name(index)
    }

    override fun headerValue(index: Int): String {
        return mRequest.headers().value(index)
    }


    override fun firstHeaderValue(name: String): String? {
        return mRequest.header(name)
    }

}