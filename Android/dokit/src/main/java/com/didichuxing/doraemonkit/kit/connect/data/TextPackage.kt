package com.didichuxing.doraemonkit.kit.connect.data


/**
 * didi Create on 2022/4/12 .
 *
 * Copyright (c) 2022/4/12 by didiglobal.com.
 *
 * @author <a href="realonlyone@126.com">zhangjun</a>
 * @version 1.0
 * @Date 2022/4/12 6:07 下午
 * @Description 用一句话说明文件功能
 */
data class TextPackage(
    val pid: String,
    val type: PackageType,
    val data: String,
    val channelSerial: String = "android",
    var connectSerial: String = "",
    var contentType: String = "text",
    var message: String = "",
    var code: Int = 0
)


