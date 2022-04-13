package com.didichuxing.doraemonkit.kit.test.mock.proxy

import com.didichuxing.doraemonkit.kit.connect.DoKitSConnectManager
import com.didichuxing.doraemonkit.kit.test.mock.MockManager
import com.didichuxing.doraemonkit.kit.connect.data.PackageType
import com.didichuxing.doraemonkit.kit.connect.data.TextPackage
import com.didichuxing.doraemonkit.kit.test.util.RandomIdentityUtils
import com.didichuxing.doraemonkit.util.GsonUtils
import com.didichuxing.doraemonkit.util.LogHelper
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

object ProxyMockManager {

    private const val TAG = "McProxyManager"

    private val mutableMap: MutableMap<String, ProxyQueryData> = mutableMapOf()

    fun requestStart(request: ProxyRequest) {
        LogHelper.d(TAG, "PROXY requestStart() request=$request")
        val proxyPackage = TextPackage(
            pid = RandomIdentityUtils.createPid(),
            type = PackageType.DATA,
            data = GsonUtils.toJson(request),
            contentType = "request",
            connectSerial = DoKitSConnectManager.getConnectSerial()
        )
        MockManager.sendDataProxy(GsonUtils.toJson(proxyPackage))
    }

    fun requestStop(response: ProxyResponse) {
        LogHelper.d(TAG, "PROXY requestStop() response=$response")
        val proxyPackage = TextPackage(
            pid = RandomIdentityUtils.createPid(),
            type = PackageType.DATA,
            data = GsonUtils.toJson(response),
            contentType = "response",
            connectSerial = DoKitSConnectManager.getConnectSerial()
        )
        MockManager.sendDataProxy(GsonUtils.toJson(proxyPackage))
    }

    private fun requestQueryLine(pid: String, request: ProxyRequest) {
        LogHelper.d(TAG, "PROXY requestQueryLine() request=$request")
        val proxyPackage = TextPackage(
            pid = pid,
            type = PackageType.DATA,
            data = GsonUtils.toJson(request),
            contentType = "query",
            connectSerial = DoKitSConnectManager.getConnectSerial()
        )
        MockManager.sendDataProxy(GsonUtils.toJson(proxyPackage))
    }


    fun requestQueryNow(request: ProxyQueryData) {
        mutableMap[request.pid] = request
        requestQueryLine(request.pid, request.proxyRequest)
    }

    fun dispatch(text: String) {
        val proxyPackage = GsonUtils.fromJson<TextPackage>(
            text,
            TextPackage::class.java
        )
        val data = proxyPackage.data

        val queryData = mutableMap[proxyPackage.pid]
        if (queryData != null) {
            if (data != null && data.isNotEmpty()) {
                val response = GsonUtils.fromJson<ProxyResponse>(data, ProxyResponse::class.java)
                queryData.proxyCallback.onResponse(response)
            } else {
                queryData.proxyCallback.onResponse(ProxyMockUtils.createEmptyProxyResponse(""))
            }
            mutableMap.remove(proxyPackage.pid)
        }

    }

    suspend inline fun <reified T> requestQuery(request: ProxyRequest): ProxyResponse = suspendCoroutine {
        try {
            val proxyQuery = ProxyQueryData(RandomIdentityUtils.createPid(), request, object : ProxyCallback {
                override fun onResponse(proxyResponse: ProxyResponse) {
                    it.resume(proxyResponse)
                }
            })
            requestQueryNow(proxyQuery)
        } catch (e: Exception) {
            it.resumeWithException(e)
        }

    }

}
