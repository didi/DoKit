package com.didichuxing.doraemonkit.kit.test.report


/**
 * didi Create on 2022/4/6 .
 *
 * Copyright (c) 2022/4/6 by didiglobal.com.
 *
 * @author <a href="realonlyone@126.com">zhangjun</a>
 * @version 1.0
 * @Date 2022/4/6 5:14 下午
 * @Description 自动化测试通信消息
 */

data class AutoTestMessage(
    val message: String = "",
    val command: String = "",
    var params: MutableMap<String, String> = mutableMapOf()
)

