package com.didichuxing.doraemonkit.kit.test.event


/**
 * didi Create on 2022/4/11 .
 *
 * Copyright (c) 2022/4/11 by didiglobal.com.
 *
 * @author <a href="realonlyone@126.com">zhangjun</a>
 * @version 1.0
 * @Date 2022/4/11 6:37 下午
 * @Description 用一句话说明文件功能
 */

enum class EventType {
    /**
     * app 切换到前台
     */
    APP_ON_FOREGROUND,

    /**
     * app 切换到后台
     */
    APP_ON_BACKGROUND,

    /**
     * 测试事件
     */
    WSE_TEST,

    /**
     * Activity 返回键事件
     */
    ACTIVITY_BACK_PRESSED,

    /**
     * Activity finish
     */
    ACTIVITY_FINISH,

    /**
     * WS 连接成功事件
     */
    WSE_CONNECTED,

    /**
     * WS 断开连接
     */
    WSE_CLOSE,

    /**
     * WS HOST 断开连接
     */
    WSE_HOST_CLOSE,

    /**
     * 通用手势事件
     */
    WSE_COMMON_EVENT,

    /**
     * 自定义手势事件
     */
    WSE_CUSTOM_EVENT,

    /**
     * TCP 消息事件
     */
    WSE_TCP_EVENT,

    /**
     * 从机执行指定事件失败
     */
    WSE_PERFORM_FAIL
}
