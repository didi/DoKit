package com.didichuxing.doraemonkit.kit.test.mock.proxy

import com.didichuxing.doraemonkit.kit.connect.DoKitSConnectManager
import com.didichuxing.doraemonkit.kit.test.mock.MockManager
import com.didichuxing.doraemonkit.kit.connect.data.PackageType
import com.didichuxing.doraemonkit.kit.connect.data.TextPackage
import com.didichuxing.doraemonkit.kit.test.util.RandomIdentityUtils
import com.didichuxing.doraemonkit.util.GsonUtils
import com.didichuxing.doraemonkit.util.LogHelper

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

class ProxyMockManager {

    companion object {
        private const val TAG = "McProxyManager"
    }

    private val mutableMap: MutableMap<String, ProxyQueryData> = mutableMapOf()

    fun requestStart(request: ProxyRequest) {
        LogHelper.d(TAG, "PROXY requestStart() request=$request")
        val textPackage = TextPackage(
            pid = RandomIdentityUtils.createPid(),
            type = PackageType.DATA,
            data = GsonUtils.toJson(request),
            contentType = "request",
            connectSerial = DoKitSConnectManager.getConnectSerial()
        )
        MockManager.sendMockTextPackage(textPackage)
    }

    fun requestStop(response: ProxyResponse) {
        LogHelper.d(TAG, "PROXY requestStop() response=$response")
        val textPackage = TextPackage(
            pid = RandomIdentityUtils.createPid(),
            type = PackageType.DATA,
            data = GsonUtils.toJson(response),
            contentType = "response",
            connectSerial = DoKitSConnectManager.getConnectSerial()
        )
        MockManager.sendMockTextPackage(textPackage)
    }

    fun requestQuery(request: ProxyRequest, callback: ProxyCallback) {
        requestQueryNow(ProxyQueryData(RandomIdentityUtils.createPid(), request, callback))
    }


    private fun requestQueryNow(request: ProxyQueryData) {
        mutableMap[request.pid] = request
        requestQueryLine(request.pid, request.proxyRequest)
    }

    private fun requestQueryLine(pid: String, request: ProxyRequest) {
        LogHelper.d(TAG, "PROXY requestQueryLine() request=$request")
        val textPackage = TextPackage(
            pid = pid,
            type = PackageType.DATA,
            data = GsonUtils.toJson(request),
            contentType = "query",
            connectSerial = DoKitSConnectManager.getConnectSerial()
        )
        MockManager.sendMockTextPackage(textPackage)
    }


    fun receiveQueryResponse(textPackage: TextPackage, response: ProxyResponse) {
        val queryData = mutableMap[textPackage.pid]
        if (queryData != null) {
            queryData.proxyCallback.onResponse(response)
            mutableMap.remove(textPackage.pid)
        }
    }


}
