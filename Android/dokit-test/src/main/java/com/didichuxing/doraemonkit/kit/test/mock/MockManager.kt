package com.didichuxing.doraemonkit.kit.test.mock

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
