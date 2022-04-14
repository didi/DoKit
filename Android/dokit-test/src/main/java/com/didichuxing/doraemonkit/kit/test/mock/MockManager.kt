package com.didichuxing.doraemonkit.kit.test.mock

import com.didichuxing.doraemonkit.kit.connect.data.TextPackage
import com.didichuxing.doraemonkit.kit.test.mock.proxy.*
import com.didichuxing.doraemonkit.util.GsonUtils

object MockManager {

    var MC_CASE_ID: String = ""

    var httpMockInterceptor: HttpMockInterceptor? = null

    var proxyMockCallback: ProxyMockCallback? = null

    private val proxyMockManager: ProxyMockManager = ProxyMockManager()

    private val onHttpProxyMockDataListenerSet: MutableSet<OnHttpProxyMockDataListener> = mutableSetOf()
    private val onHttpProxyMockSendListenerSet: MutableSet<OnHttpProxyMockSendListener> = mutableSetOf()

    /**
     * 发送模拟数据
     */
    fun sendMockTextPackage(textPackage: TextPackage) {
        val data = GsonUtils.toJson(textPackage)
        proxyMockCallback?.let {
            try {
                it.send(data)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        onHttpProxyMockSendListenerSet.forEach {
            try {
                it.onHttpProxyMockSend(textPackage)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun requestStart(request: ProxyRequest) {
        proxyMockManager.requestStart(request)

        onHttpProxyMockDataListenerSet.forEach {
            try {
                it.onHttpProxyMockRequest(request, true)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun requestStop(response: ProxyResponse) {
        proxyMockManager.requestStop(response)

        onHttpProxyMockDataListenerSet.forEach {
            try {
                it.onHttpProxyMockResponse(response, true)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun requestQuery(request: ProxyRequest, callback: ProxyCallback) {
        proxyMockManager.requestQuery(request, callback)

        onHttpProxyMockDataListenerSet.forEach {
            try {
                it.onHttpProxyMockRequest(request, false)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun receiveQueryResponse(text: String) {
        val textPackage = GsonUtils.fromJson<TextPackage>(
            text,
            TextPackage::class.java
        )
        receiveQueryResponse(textPackage)
    }

    fun receiveQueryResponse(textPackage: TextPackage) {

        val data = textPackage.data
        val response = if (data != null && data.isNotEmpty()) {
            GsonUtils.fromJson<ProxyResponse>(data, ProxyResponse::class.java)
        } else {
            ProxyMockUtils.createEmptyProxyResponse("")
        }
        proxyMockManager.receiveQueryResponse(textPackage, response)

        onHttpProxyMockDataListenerSet.forEach {
            try {
                it.onHttpProxyMockResponse(response, false)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


}
