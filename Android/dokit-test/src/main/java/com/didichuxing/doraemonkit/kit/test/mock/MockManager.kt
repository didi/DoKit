package com.didichuxing.doraemonkit.kit.test.mock

object MockManager {


    var MC_CASE_ID: String = ""

    var httpMockInterceptor: HttpMockInterceptor? = null

    var proxyMockCallback: ProxyMockCallback? = null


    fun sendDataProxy(data: String) {
        proxyMockCallback?.let {
            it.send(data)
        }
    }



}
