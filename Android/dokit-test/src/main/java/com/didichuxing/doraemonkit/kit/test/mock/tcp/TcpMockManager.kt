package com.didichuxing.doraemonkit.kit.test.mock.tcp

import com.didichuxing.doraemonkit.kit.test.DoKitTestManager
import com.didichuxing.doraemonkit.kit.test.event.monitor.TcpMessageEventMonitor


/**
 * didi Create on 2022/1/21 .
 *
 * Copyright (c) 2022/1/21 by didiglobal.com.
 *
 * @author <a href="realonlyone@126.com">zhangjun</a>
 * @version 1.0
 * @Date 2022/1/21 2:54 下午
 * @Description TCP 数据mock管理类，实现数据的
 */

object TcpMockManager {


    var tcpMockMessageProcessor: TcpMockMessageProcessor? = null

    fun hookTcpSendMessageEvent(message: String): Boolean {
        //从机收发都拦截不处理
        if (DoKitTestManager.isClientMode()) {
            return true
        }
        if (DoKitTestManager.isHostMode()) {
            TcpMessageEventMonitor.onTcpMessageEvent("send", message)
        }
        return false
    }

    fun hookTcpReceiveMessageEvent(message: String): Boolean {
        //从机收发都拦截不处理
        if (DoKitTestManager.isClientMode()) {
            return true
        }
        if (DoKitTestManager.isHostMode()) {
            TcpMessageEventMonitor.onTcpMessageEvent("receive", message)
        }
        return false
    }

    /**
     * 从机接收到Mock数据处理
     */
    fun onTcpMessageEvent(type: String, message: String) {
        if (tcpMockMessageProcessor != null) {
            tcpMockMessageProcessor?.onTcpMessageEvent(type, message)
        }
    }

}
