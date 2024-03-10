package com.didichuxing.doraemonkit.kit.test.event

/**
 * didi Create on 2022/4/13 .
 *
 * Copyright (c) 2022/4/13 by didiglobal.com.
 *
 * @author <a href="realonlyone@126.com">zhangjun</a>
 * @version 1.0
 * @Date 2022/4/13 3:07 下午
 * @Description 窗口信息，帮助精确查找
 */
data class WindowNode(
    val name: String = "", //通常获取到的是 activity 的类名称
    val windowId: String = "",//window 唯一识别id，通过id来实现快速查找窗口
    val type: Int = 0,//窗口类型  0，其他，1：activity ，2：Dialog
    val rootViewName: String = "",
    var index: Int = 0

)
