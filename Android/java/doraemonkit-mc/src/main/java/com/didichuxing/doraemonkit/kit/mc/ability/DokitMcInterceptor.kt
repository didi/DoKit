package com.didichuxing.doraemonkit.kit.mc.ability

import com.didichuxing.doraemonkit.constant.DoKitConstant
import com.didichuxing.doraemonkit.constant.WSMode
import com.didichuxing.doraemonkit.extension.doKitGlobalScope
import com.didichuxing.doraemonkit.extension.sortedByKey
import com.didichuxing.doraemonkit.extension.toMap
import com.didichuxing.doraemonkit.kit.network.NetworkManager
import com.didichuxing.doraemonkit.kit.network.okhttp.InterceptorUtil
import com.didichuxing.doraemonkit.kit.network.okhttp.interceptor.AbsDoKitInterceptor
import com.didichuxing.doraemonkit.util.EncodeUtils
import com.didichuxing.doraemonkit.util.GsonUtils
import com.didichuxing.doraemonkit.util.LogHelper
import kotlinx.coroutines.launch
import okhttp3.*
import okio.ByteString
import java.net.URLDecoder
import java.util.*

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2021/6/7-20:03
 * 描    述：一机多控抓包拦截器
 * 修订历史：
 * ================================================
 */
class DokitMcInterceptor : AbsDoKitInterceptor() {
    override fun intercept(chain: Interceptor.Chain): Response {
        //数据采集
        val request = chain.request()
        val response = chain.proceed(request)
        val url = request.url()
        val host: String = url.host()
        val contentType = response.header("Content-Type") ?: response.header("content-type")
        //如果是图片则不进行拦截
        if (InterceptorUtil.isImg(contentType)) {
            return response
        }
        //如果是mock平台的接口则不进行拦截
        if (host.equals(NetworkManager.MOCK_HOST, ignoreCase = true)) {
            return response
        }
        //不包含query字段
        val method = request.method()
        val path = URLDecoder.decode(url.encodedPath(), "utf-8")
        val fragment = url.fragment() ?: "null"
        val queryMap = createQueryMap(url)

        val strQuery = GsonUtils.toJson(queryMap)
        val requestContentType = if (request.body() == null) {
            "null"
        } else {
            request.body()?.contentType().toString().toLowerCase(Locale.ROOT)
        }

        val requestBodyMap = createRequestBodyMap(request)
        val strRequestBody = GsonUtils.toJson(requestBodyMap)
        val strResponseBody = response.peekBody(Long.MAX_VALUE).string()
        val k =
            "method=$method&path=$path&fragment=$fragment&query=$strQuery&contentType=$requestContentType&requestBody=$strRequestBody"
        val key = ByteString.encodeUtf8(k).md5().hex()
        when (DoKitConstant.WS_MODE) {
            WSMode.RECORDING -> {
                //数据采集
                // val responseBody4Base64 = String(EncodeUtils.base64Encode(strResponseBody))
                //todo: 实时发送网络请求
                doKitGlobalScope.launch {
                    val httInfo = HttpInfo(
                        DoKitConstant.PRODUCT_ID,
                        "caseId",
                        key,
                        method,
                        path,
                        fragment,
                        requestContentType,
                        queryMap,
                        requestBodyMap,
                        strResponseBody
                    )

                    val resulit = McHttpManager.uploadHttpInfo<Any>(httInfo)

                }


            }
            WSMode.HOST,
            WSMode.CLIENT -> {
                if (McHttpManager.mHttpInfoMap[key] != null) {
                    val responseBodyBytes =
                        EncodeUtils.base64Decode(McHttpManager.mHttpInfoMap[key]?.responseBody4base64)
                    val responseBody =
                        ResponseBody.create(response.body()?.contentType(), responseBodyBytes)
                    return response.newBuilder()
                        .code(response.code())
                        .request(request)
                        .message(response.message())
                        .protocol(response.protocol())
                        .headers(response.headers())
                        .body(responseBody)
                        .build()
                }
            }
            else -> {
                return response
            }
        }


        return response
    }


    private fun createQueryMap(url: HttpUrl): Map<String, String> {
        val maps = url.query()?.toMap()
        McHttpManager.mExcludeKey.forEach {
            maps?.remove(it)
        }
        return maps?.sortedByKey() ?: mapOf()

    }


    private fun createRequestBodyMap(request: Request): Map<String, String> {
        val maps = request.body().toMap()
        McHttpManager.mExcludeKey.forEach {
            maps.remove(it)
        }

        return maps.sortedByKey()

    }


}