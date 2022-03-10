package com.didichuxing.doraemonkit.rpc.ability

import com.didichuxing.doraemonkit.kit.core.DoKitManager
import com.didichuxing.doraemonkit.constant.WSMode
import com.didichuxing.doraemonkit.kit.mc.mock.proxy.*
import com.didichuxing.doraemonkit.kit.network.rpc.AbsDoKitRpcInterceptor
import com.didichuxing.doraemonkit.util.LogHelper
import didihttp.*
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.runBlocking
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
 * @Description  网络使用代理方式的一机多控
 */
class RpcMcProxyInterceptor : AbsDoKitRpcInterceptor() {
    private val mExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        LogHelper.e(TAG, "error message: ${throwable.message}")
    }


    override fun intercept(chain: Interceptor.Chain): Response {

        val request = chain.request()
        if (DoKitManager.WS_MODE == WSMode.HOST || DoKitManager.WS_MODE == WSMode.CLIENT) {

            if (!ProxyUtils.filterRequest(request)) {
                val did = IdentityUtils.createDid()
                val proxyRequest = ProxyUtils.createProxyRequest(did, request)

                if (DoKitManager.WS_MODE == WSMode.HOST) {
                    //主机处理方式
                    McProxyManager.requestStart(proxyRequest)
                    var response: Response
                    try {
                        response = chain.proceed(request)
                    } catch (e: Exception) {
                        val proxyResponse = ProxyUtils.createEmptyProxyResponse(did)
                        McProxyManager.requestStop(proxyResponse)
                        throw IOException("dokit mock error", e)
                    }
                    val proxyResponse = ProxyUtils.createProxyResponse(did, response)
                    McProxyManager.requestStop(proxyResponse)
                    return response
                } else {
                    //从机处理方式
                    runBlocking(mExceptionHandler) {
                        val proxyRequest = ProxyUtils.createProxyRequest("", request)
                        LogHelper.i(TAG, "PROXY start proxyRequest=${proxyRequest}")
                        val proxyResponse: ProxyResponse = McProxyManager.requestQuery<ProxyResponse>(proxyRequest)
                        LogHelper.i(TAG, "PROXY stop proxyResponse=${proxyResponse}")

                        //查询不到数据，直接查询结果
                        if (proxyResponse.image || proxyResponse.protocol.equals("mock", ignoreCase = false)) {
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
                            .protocol(Protocol.valueOf(proxyResponse.protocol))
                            .headers(Headers.of(proxyResponse.responseHeaders))
                            .body(responseBody)
                            .build()
                    }
                }

            }

        }

        return chain.proceed(request)
    }


}