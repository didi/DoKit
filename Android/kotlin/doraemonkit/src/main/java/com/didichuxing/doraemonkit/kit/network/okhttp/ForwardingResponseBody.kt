package com.didichuxing.doraemonkit.kit.network.okhttp

import okhttp3.MediaType
import okhttp3.ResponseBody
import okio.BufferedSource
import okio.Okio
import java.io.InputStream

class ForwardingResponseBody(private val mBody: ResponseBody, interceptedStream: InputStream?) : ResponseBody() {
    private val mInterceptedSource: BufferedSource = Okio.buffer(Okio.source(interceptedStream))
    override fun contentType(): MediaType? {
        return mBody.contentType()
    }

    override fun contentLength(): Long {
        return mBody.contentLength()
    }

    override fun source(): BufferedSource {
        return mInterceptedSource
    }

}