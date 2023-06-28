package com.didichuxing.doraemonkit.plugin.extension


/**
 * didi Create on 2023/3/21 .
 *
 * Copyright (c) 2023/3/21 by didiglobal.com.
 *
 * @author <a href="realonlyone@126.com">zhangjun</a>
 * @version 1.0
 * @Date 2023/3/21 6:10 下午
 * @Description 用一句话说明文件功能
 */

open class WebViewExtension(
    var network: Boolean = true,
    var dokitWeb: Boolean = false,
    var vConsole: Boolean = false
) {

    fun network(boolean: Boolean) {
        network = boolean
    }

    fun dokitWeb(boolean: Boolean) {
        dokitWeb = boolean
    }

    fun vConsole(boolean: Boolean) {
        vConsole = boolean
    }

    override fun toString(): String {
        return "WebViewExtension(network=$network, dokitWeb=$dokitWeb, vConsole=$vConsole)"
    }


}
