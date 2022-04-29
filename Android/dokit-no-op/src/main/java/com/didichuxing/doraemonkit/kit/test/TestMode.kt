package com.didichuxing.doraemonkit.kit.test

/**
 * 测试工作模式 mode
 */
enum class TestMode {
    /**
     *未知 :不允许执行采集也不允许执行模拟事件
     */
    UNKNOWN,

    /**
     * 主机:采集事件
     */
    HOST,

    /**
     * 从机:模拟执行事件
     */
    CLIENT,

}
