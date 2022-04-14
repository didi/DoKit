package com.didichuxing.doraemonkit.kit.mc.ui

/**
 * 测试工作模式 mode
 */
enum class McPages {

    /**
     *
     */
    MAIN,
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
    MC_CASELIST,

}
