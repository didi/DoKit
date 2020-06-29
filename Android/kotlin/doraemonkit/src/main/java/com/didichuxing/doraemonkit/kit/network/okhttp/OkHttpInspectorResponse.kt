package com.didichuxing.doraemonkit.kit.network.okhttp

import com.didichuxing.doraemonkit.kit.network.okhttp.core.NetworkInterpreter.InspectorResponse
import okhttp3.Request
import okhttp3.Response

class OkHttpInspectorResponse(
        private val mRequestId: Int,
        private val mRequest: Request,
        private val mResponse: Response) : InspectorResponse {
    override fun requestId(): Int {
        return mRequestId
    }

    override fun url(): String? {
        return mRequest.url().toString()
    }

    override fun statusCode(): Int {
        return mResponse.code()
    }

    override fun headerCount(): Int {
        return mResponse.headers().size()
    }

    override fun headerName(index: Int): String {
        return mResponse.headers().name(index)
    }

    override fun headerValue(index: Int): String {
        return mResponse.headers().value(index)
    }


    override fun firstHeaderValue(name: String): String? {
        return mResponse.header(name)
    }

}