package com.didichuxing.doraemonkit.kit.connect.ws


/**
 * didi Create on 2022/4/12 .
 *
 * Copyright (c) 2022/4/12 by didiglobal.com.
 *
 * @author <a href="realonlyone@126.com">zhangjun</a>
 * @version 1.0
 * @Date 2022/4/12 2:28 下午
 * @Description 用一句话说明文件功能
 */

enum class ConnectStatus {

    /**
     * 链接准备中
     */
    PREPARE,

    /**
     * 离线
     */
    OFF_LINE,

    /**
     * 链接中
     */
    CONNECT,

    /**
     * 链接失败
     */
    FAILED
}
