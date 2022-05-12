package com.didichuxing.doraemonkit.kit.test.mock

import com.didichuxing.doraemonkit.kit.connect.data.TextPackage
import com.didichuxing.doraemonkit.kit.test.TestMode
import com.didichuxing.doraemonkit.kit.test.mock.proxy.*
import com.didichuxing.doraemonkit.kit.test.utils.XposedHookUtil
import com.didichuxing.doraemonkit.util.GsonUtils


/**
 * didi Create on 2022/4/14 .
 *
 * Copyright (c) 2022/4/14 by didiglobal.com.
 *
 * @author <a href="realonlyone@126.com">zhangjun</a>
 * @version 1.0
 * @Date 2022/4/14 10:52 上午
 * @Description 数据mocl功能管理类，管理一机多控及自动化测试数据
 */

object MockManager {

    @Deprecated("")
    var MC_CASE_ID: String = ""


    var httpMockInterceptor: HttpMockInterceptor? = null

    var proxyMockCallback: ProxyMockCallback? = null

    private val proxyMockManager: ProxyMockManager = ProxyMockManager()

    private val onHttpProxyMockDataListenerSet: MutableSet<OnHttpProxyMockDataListener> = mutableSetOf()
    private val onHttpProxyMockSendListenerSet: MutableSet<OnHttpProxyMockSendListener> = mutableSetOf()

    private var testMode: TestMode = TestMode.UNKNOWN


    /**
     * 是否是主机模式
     */
    fun isHostMode(): Boolean {
        return testMode == TestMode.HOST
    }

    /**
     * 是否是从机模式
     */
    fun isClientMode(): Boolean {
        return testMode == TestMode.CLIENT
    }

    /**
     * 测试功能是否关闭
     */
    fun isClose(): Boolean {
        return testMode == TestMode.UNKNOWN
    }

    fun getTestMode(): TestMode {
        return testMode
    }

    /**
     * 开始测试功能
     * 1、开始hook 或者关闭hook
     */
    fun startTest(testMode: TestMode) {
        if (testMode == TestMode.HOST) {
            startTestByHostMode()
        } else if (testMode == TestMode.CLIENT) {
            startTestByClientMode()
        } else {
            closeTest()
        }
    }

    /**
     * 关闭测试功能
     * 备注：关闭后测试相关功能将不工作
     */
    fun closeTest() {
        testMode = TestMode.UNKNOWN
        if (XposedHookUtil.isRunTimeHookEnable()) {
            XposedHookUtil.stopRunTimeHook()
        }
    }


    private fun startTestByHostMode() {
        testMode = TestMode.HOST
    }

    private fun startTestByClientMode() {
        testMode = TestMode.CLIENT
    }

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
