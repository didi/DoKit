package com.didichuxing.doraemonkit.kit.test.mock.http

import com.didichuxing.doraemonkit.kit.test.mock.proxy.*
import com.didichuxing.doraemonkit.kit.network.okhttp.interceptor.AbsDoKitInterceptor
import com.didichuxing.doraemonkit.kit.test.mock.MockManager
import com.didichuxing.doraemonkit.kit.test.utils.RandomIdentityUtil
import com.didichuxing.doraemonkit.util.LogHelper
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.runBlocking
import okhttp3.*
import java.io.IOException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

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
        if (MockManager.isHostMode() || MockManager.isClientMode()) {
            if (!ProxyMockUtils.filterRequest(request)) {
                if (MockManager.isHostMode()) {
                    //主机处理方式
                    val did = RandomIdentityUtil.createDid()
                    val proxyRequest = ProxyMockUtils.createProxyRequest(did, request)
                    MockManager.requestStart(proxyRequest)
                    var response: Response
                    try {
                        response = chain.proceed(request)
                    } catch (e: Exception) {
                        val proxyResponse = ProxyMockUtils.createEmptyProxyResponse(did)
                        MockManager.requestStop(proxyResponse)
                        throw IOException("dokit mock error", e)
                    }
                    val proxyResponse = ProxyMockUtils.createProxyResponse(did, response)
                    MockManager.requestStop(proxyResponse)
                    return response
                } else {
                    //从机处理方式
                    return runBlocking(mExceptionHandler) {
                        val proxyRequest = ProxyMockUtils.createProxyRequest("", request)
                        LogHelper.i(TAG, "PROXY start proxyRequest=${proxyRequest}")
                        val proxyResponse: ProxyResponse = requestQuery<ProxyResponse>(proxyRequest)
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

    /**
     * 将异步请求转为阻塞等待
     */
    private suspend inline fun <reified T> requestQuery(request: ProxyRequest): ProxyResponse = suspendCoroutine {
        try {
            val proxyQuery = object : ProxyCallback {
                override fun onResponse(proxyResponse: ProxyResponse) {
                    it.resume(proxyResponse)
                }
            }
            MockManager.requestQuery(request, proxyQuery)
        } catch (e: Exception) {
            it.resumeWithException(e)
        }
    }


}
