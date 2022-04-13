package com.didichuxing.doraemonkit.kit.connect

/**
 * didi Create on 2022/1/18 .
 *
 * Copyright (c) 2022/1/18 by didiglobal.com.
 *
 * @author <a href="realonlyone@126.com">zhangjun</a>
 * @version 1.0
 * @Date 2022/1/18 8:12 下午
 * @Description 用一句话说明文件功能
 */

data class ConnectAddress(
    val name: String,
    val url: String = "",
    val time: String,
    var enable: Boolean = false,
    var connectSerial: String = ""
)
