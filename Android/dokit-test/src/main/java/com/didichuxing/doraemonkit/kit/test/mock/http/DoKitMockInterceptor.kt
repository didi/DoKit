package com.didichuxing.doraemonkit.kit.test.mock.http

import com.didichuxing.doraemonkit.kit.core.DoKitManager
import com.didichuxing.doraemonkit.kit.test.TestMode
import com.didichuxing.doraemonkit.extension.sortedByKey
import com.didichuxing.doraemonkit.extension.toMap
import com.didichuxing.doraemonkit.kit.test.mock.HttpMockInterceptor
import com.didichuxing.doraemonkit.kit.test.mock.data.HttpMatchedInfo
import com.didichuxing.doraemonkit.kit.test.mock.data.McMockKey
import com.didichuxing.doraemonkit.kit.test.mock.MockManager
import com.didichuxing.doraemonkit.kit.network.NetworkManager
import com.didichuxing.doraemonkit.kit.network.okhttp.InterceptorUtil
import com.didichuxing.doraemonkit.kit.network.okhttp.interceptor.AbsDoKitInterceptor
import com.didichuxing.doraemonkit.kit.test.DoKitTestManager
import com.didichuxing.doraemonkit.util.GsonUtils
import com.didichuxing.doraemonkit.util.LogHelper
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.runBlocking
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
@Deprecated("DoKitProxyMockInterceptor")
class DoKitMockInterceptor : AbsDoKitInterceptor() {
    private val mExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        LogHelper.e(TAG, "error message: ${throwable.message}")
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        //数据采集
        val request = chain.request()
        val response = chain.proceed(request)
        try {
            if (DoKitTestManager.isClose()) {
                return response
            }
            val url = request.url()
            val host: String = url.host()
            val scheme = url.scheme()
            val contentType = response.header("Content-Type") ?: response.header("content-type")
            //如果是图片则不进行拦截
            if (InterceptorUtil.isImg(contentType)) {
                return response
            }
            //如果是mock平台的接口则不进行拦截
            if (host.equals(NetworkManager.MOCK_HOST, ignoreCase = true)) {
                return response
            }

            //如果是一机多控相关接口则不拦截
            if ("$scheme://$host".equals(HttpMockServer.host, ignoreCase = true)) {
//                val strResponseBody = response.peekBody(Long.MAX_VALUE).string()
//                LogHelper.i(TAG, "========DoKit SDK 接口数据 Start url:$url ========")
//                LogHelper.json(TAG, strResponseBody)
//                LogHelper.i(TAG, "========DoKit SDK 接口数据 End url:$url ========")
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
//            LogHelper.i(TAG, "========业务接口 Start url:$url ========")
//            LogHelper.json(TAG, strResponseBody)
//            LogHelper.i(TAG, "========业务接口 End url:$url ========")
            val k = "method=$method&path=$path&fragment=$fragment&query=$strQuery&contentType=$requestContentType&requestBody=$strRequestBody"
            LogHelper.i(TAG, "originKey===>$k")
            val key = ByteString.encodeUtf8(k).md5().hex()
            when (DoKitTestManager.getTestMode()) {
//                TestMode.RECORDING -> {
//                    //数据采集
//                    // val responseBody4Base64 = String(EncodeUtils.base64Encode(strResponseBody))
//                    doKitGlobalScope.launch(mExceptionHandler) {
//                        val httInfo = HttpUploadInfo(
//                            DoKitManager.PRODUCT_ID,
//                            MockManager.MC_CASE_ID,
//                            k,
//                            key,
//                            method,
//                            path,
//                            fragment,
//                            requestContentType,
//                            queryMap,
//                            requestBodyMap,
//                            strResponseBody
//                        )
//
//                        val result = McHttpManager.uploadHttpInfo<Any>(httInfo)
//                        if (result.code != McHttpManager.RESPONSE_OK) {
//                            LogHelper.e(TAG, "e===>${result.msg}")
//                        }
//                    }
//                }
//                WSMode.CLIENT,
                TestMode.HOST -> {
                    if (MockManager.MC_CASE_ID.isNotBlank() && DoKitManager.PRODUCT_ID.isNotBlank()) {
                        //将挂起函数转为阻塞调用 等待协程返回值
                        return runBlocking(mExceptionHandler) {
                            try {
                                var result = HttpMockServer.httpMatch<HttpMatchedInfo>(key)

                                val interceptor: HttpMockInterceptor? = MockManager.httpMockInterceptor
                                if (interceptor != null) {
                                    result = interceptor.intercept(McMockKey(key, k, strQuery, strRequestBody), result)

                                }
                                LogHelper.i(TAG, "MCMOCKLOG OK key=$key,code=${result.code} originKey===>$k data=${result.data}")
                                if (result.code == HttpMockServer.RESPONSE_OK && result.data != null) {
                                    val responseBody =
                                        ResponseBody.create(
                                            response.body()?.contentType(),
                                            result.data!!.responseBody
                                        )
                                    return@runBlocking response.newBuilder()
                                        .code(response.code())
                                        .request(request)
                                        .message(response.message())
                                        .protocol(response.protocol())
                                        .headers(response.headers())
                                        .body(responseBody)
                                        .build()
                                } else {
                                    LogHelper.i(TAG, "MCMCOKLOG  key=$key,code=${result.code} originKey===>$k data=${result.data}")
                                    return@runBlocking response
                                }
                            } catch (e: Exception) {
                                LogHelper.e(TAG, "IXP:mock error2 all url:$url,msg=${e.message}")
                                return@runBlocking response
                            }

                        }
                    }
                }
                else -> {
                    LogHelper.e(TAG, "MCMCOKLOG: mock error3 all url:$url")
                    return response
                }
            }
            return response
        } catch (e: Exception) {
            return response
        }
    }


    private fun createQueryMap(url: HttpUrl): Map<String, String> {
        val maps = url.query()?.toMap()
        HttpMockServer.mExcludeKey.forEach {
            maps?.remove(it)
        }
        return maps?.sortedByKey() ?: mapOf()
    }


    private fun createRequestBodyMap(request: Request): Map<String, String> {
        val maps = request.body().toMap()
        HttpMockServer.mExcludeKey.forEach {
            maps.remove(it)
        }
        return maps.sortedByKey()
    }


}
