package com.didichuxing.doraemonkit.kit.mc.mock

import com.didichuxing.doraemonkit.kit.mc.all.McNetMockInterceptor
import com.didichuxing.doraemonkit.kit.mc.all.McTcpMessageProcessor

object MockManager {


    var MC_CASE_ID: String = ""

    var mcNetMockInterceptor: McNetMockInterceptor? = null

    var mcTcpMessageProcessor: McTcpMessageProcessor? = null

    var mockProxyDataClient: MockProxyDataClient? = null


    fun sendDataProxy(data: String) {
        mockProxyDataClient?.let {
            it.send(data)
        }
    }
}
