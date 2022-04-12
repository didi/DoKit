package com.didichuxing.doraemonkit.kit.mc.net


/**
 * didi Create on 2022/2/16 .
 *
 * Copyright (c) 2022/2/16 by didiglobal.com.
 *
 * @author <a href="realonlyone@126.com">zhangjun</a>
 * @version 1.0
 * @Date 2022/2/16 5:09 下午
 * @Description 通过PC服务端转发的协议包类型
 */

enum class ConnectMode {
    /**
     * 关闭
     * 用于连接成功后认证
     */
    CLOSE,
    /**
     *
     * 成功
     */
    CONNECT,

    /**
     * 登陆
     */
    LOGIN,
}
