package com.didichuxing.doraemonkit.constant

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020/11/17-17:11
 * 描    述：事件对象类型
 * 修订历史：
 * ================================================
 */


/**
 * ws mode
 */
enum class WSMode {
    /**
     * 位置类型，即为连接
     */
    UNKNOW,

    /**
     * 联网
     */
    CONNECT,

    /**
     * 联网
     */
    CONNECT_HISTORY,

    /**
     * 主机
     */
    HOST,

    /**
     * 从机
     */
    CLIENT,

    /**
     * 从机链接历史
     */
    CLIENT_HISTORY,

    /**
     *数据抓取中...
     */
    RECORDING,

    /**
     * 查看用例列表
     */
    MC_CASELIST
}

/**
 * 事件类型
 */
enum class WSEType {
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
    WSE_COMM_EVENT,

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










