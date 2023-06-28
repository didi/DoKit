package com.didichuxing.doraemonkit.plugin.extension


/**
 * didi Create on 2023/3/21 .
 *
 * Copyright (c) 2023/3/21 by didiglobal.com.
 *
 * @author <a href="realonlyone@126.com">zhangjun</a>
 * @version 1.0
 * @Date 2023/3/21 5:36 下午
 * @Description 用一句话说明文件功能
 */

open class NetworkExtension(
    var okHttp: Boolean = true,
    var urlConnect: Boolean = true,
    var didiHttp: Boolean = true,
    var didiSocket: Boolean = true,
    var didiDjSocket: Boolean = true
) {


    fun okHttp(boolean: Boolean) {
        okHttp = boolean
    }

    fun urlConnect(boolean: Boolean) {
        urlConnect = boolean
    }

    fun didiHttp(boolean: Boolean) {
        didiHttp = boolean
    }

    fun didiSocket(boolean: Boolean) {
        didiSocket = boolean
    }

    fun didiDjSocket(boolean: Boolean) {
        didiDjSocket = boolean
    }

    override fun toString(): String {
        return "NetworkExtension(okHttp=$okHttp, urlConnect=$urlConnect, didiHttp=$didiHttp, didiSocket=$didiSocket, didiDjSocket=$didiDjSocket)"
    }
}
