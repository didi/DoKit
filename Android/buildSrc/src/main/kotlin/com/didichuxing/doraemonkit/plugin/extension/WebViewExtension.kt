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

class WebViewExtension(
    var network: Boolean = true,
) {

    override fun toString(): String {
        return "WebViewExtension(network=$network)"
    }
}
