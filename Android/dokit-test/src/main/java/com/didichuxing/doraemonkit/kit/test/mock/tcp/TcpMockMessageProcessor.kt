package com.didichuxing.doraemonkit.kit.test.mock.tcp


/**
 * didi Create on 2022/1/21 .
 *
 * Copyright (c) 2022/1/21 by didiglobal.com.
 *
 * @author <a href="realonlyone@126.com">zhangjun</a>
 * @version 1.0
 * @Date 2022/1/21 2:54 下午
 * @Description 用一句话说明文件功能
 */

interface TcpMockMessageProcessor {

    fun onTcpMessageEvent(type: String, message: String)
}
