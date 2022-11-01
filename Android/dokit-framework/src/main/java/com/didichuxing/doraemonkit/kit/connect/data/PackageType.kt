package com.didichuxing.doraemonkit.kit.connect.data


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

enum class PackageType(name: String, ordinal: Int) {
    /**
     * 登陆
     * 用于连接成功后认证
     */
    LOGIN("login", 1),

    /**
     * 广播
     * 用户主从数据转发广播
     */
    BROADCAST("broadcast", 2),

    /**
     * 通知
     * 用于PC服务端通知客户端
     */
    NOTIFY("notify", 3),

    /**
     * 数据代理
     */
    DATA("data", 4),

    /**
     * 自动化测试
     */
    AUTOTEST("autotest", 5),

    /**
     * 心跳
     */
    HEART_BEAT("heart_beat", 1),

    ;


    override fun toString(): String {
        super.toString()
        return name
    }

}
