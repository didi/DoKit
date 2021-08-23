package com.didichuxing.doraemonkit.okhttp_api

import okhttp3.*
import java.net.URL

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020/10/19-14:55
 * 描    述：
 * 修订历史：
 * ================================================
 */
object OkHttpWrapV3 {
    fun createHttpUrl(url: String?): HttpUrl? {
        url?.let {
            return HttpUrl.parse(it)
        } ?: return null
    }


    fun toUrl(httpUrl: HttpUrl?): URL? {
        return httpUrl?.url()
    }

    fun toResponseBody(response: Response?): ResponseBody? {
        return response?.body()
    }


    fun toHttpQuery(httpUrl: HttpUrl?): String? {
        return httpUrl?.query()
    }


    fun toEncodedPath(httpUrl: HttpUrl?): String? {
        return httpUrl?.encodedPath()
    }


    fun toRequestHost(httpUrl: HttpUrl?): String? {
        return httpUrl?.host()
    }

    fun toRequestHost(request: Request): String? {
        return request.url().host()
    }

    fun toResponseHost(response: Response): String? {
        return response.request().url().host()
    }


    fun toScheme(httpUrl: HttpUrl): String {
        return httpUrl.scheme()
    }


    fun toResponseCode(response: Response): Int {
        return response.code()
    }


    fun toMediaType(contentType: String?): MediaType? {
        if (contentType.isNullOrBlank()) {
            return null
        }
        return MediaType.parse(contentType)
    }

    fun toRequestBody(content: String?, mediaType: MediaType?): RequestBody? {
        return RequestBody.create(mediaType, content ?: "")
    }


}