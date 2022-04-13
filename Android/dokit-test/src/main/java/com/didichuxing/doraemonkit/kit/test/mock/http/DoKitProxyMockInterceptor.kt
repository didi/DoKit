package com.didichuxing.doraemonkit.kit.test.mock.http

import com.didichuxing.doraemonkit.kit.test.TestMode
import com.didichuxing.doraemonkit.kit.test.mock.proxy.*
import com.didichuxing.doraemonkit.kit.network.okhttp.interceptor.AbsDoKitInterceptor
import com.didichuxing.doraemonkit.kit.test.DoKitTestManager
import com.didichuxing.doraemonkit.kit.test.util.RandomIdentityUtils
import com.didichuxing.doraemonkit.util.LogHelper
import io.ktor.http.*
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.runBlocking
import okhttp3.*
import java.io.IOException
import java.util.*

/**
 * didi Create on 2022/3/10 .
 *
 * Copyright (c) 2022/3/10 by didiglobal.com.
 *
 * @author <a href="realonlyone@126.com">zhangjun</a>
 * @version 1.0
 * @Date 2022/3/10 7:45 下午
 * @Description 用一句话说明文件功能
 */
class DoKitProxyMockInterceptor : AbsDoKitInterceptor() {
    private val mExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        LogHelper.e(TAG, "error message: ${throwable.message}")
    }


    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        if (DoKitTestManager.WS_MODE == TestMode.HOST || DoKitTestManager.WS_MODE == TestMode.CLIENT) {
            if (!ProxyMockUtils.filterRequest(request)) {
                if (DoKitTestManager.WS_MODE == TestMode.HOST) {
                    //主机处理方式
                    val did = RandomIdentityUtils.createDid()
                    val proxyRequest = ProxyMockUtils.createProxyRequest(did, request)
                    ProxyMockManager.requestStart(proxyRequest)
                    var response: Response
                    try {
                        response = chain.proceed(request)
                    } catch (e: Exception) {
                        val proxyResponse = ProxyMockUtils.createEmptyProxyResponse(did)
                        ProxyMockManager.requestStop(proxyResponse)
                        throw IOException("dokit mock error", e)
                    }
                    val proxyResponse = ProxyMockUtils.createProxyResponse(did, response)
                    ProxyMockManager.requestStop(proxyResponse)
                    return response
                } else {
                    //从机处理方式
                    return runBlocking(mExceptionHandler) {
                        val proxyRequest = ProxyMockUtils.createProxyRequest("", request)
                        LogHelper.i(TAG, "PROXY start proxyRequest=${proxyRequest}")
                        val proxyResponse: ProxyResponse = ProxyMockManager.requestQuery<ProxyResponse>(proxyRequest)
                        LogHelper.i(TAG, "PROXY stop proxyResponse=${proxyResponse}")

                        //查询不到数据，直接查询结果
                        if (proxyResponse.image
                            || proxyResponse.protocol.equals("local", ignoreCase = false)
                            || proxyResponse.responseContentType == null
                        ) {
                            return@runBlocking chain.proceed(request)
                        }

                        val responseBody =
                            ResponseBody.create(
                                MediaType.parse(proxyResponse.responseContentType),
                                proxyResponse.responseBody
                            )
                        return@runBlocking Response.Builder()
                            .code(proxyResponse.responseCode)
                            .request(request)
                            .message("ok")
                            .protocol(Protocol.get(proxyResponse.protocol))
                            .headers(ProxyMockUtils.parseHeaders(proxyResponse.responseHeaders))
                            .body(responseBody)
                            .build()
                    }
                }
            }
        }
        return chain.proceed(request)

    }


}
