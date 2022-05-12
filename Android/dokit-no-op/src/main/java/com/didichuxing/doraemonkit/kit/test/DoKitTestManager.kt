package com.didichuxing.doraemonkit.kit.test

object DoKitTestManager {

    /**
     * 是否是主机模式
     */
    fun isHostMode(): Boolean {
        return false
    }

    /**
     * 是否是从机模式
     */
    fun isClientMode(): Boolean {
        return false
    }

    /**
     * 测试功能是否关闭
     */
    fun isClose(): Boolean {
        return true
    }

    fun getTestMode(): TestMode {
        return TestMode.UNKNOWN
    }

    /**
     * 开始测试功能
     * 1、开始hook 或者关闭hook
     */
    fun startTest(testMode: TestMode) {
    }

    /**
     * 关闭测试功能
     * 备注：关闭后测试相关功能将不工作
     */
    fun closeTest() {
    }
}
