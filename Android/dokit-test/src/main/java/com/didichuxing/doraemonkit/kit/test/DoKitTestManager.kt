package com.didichuxing.doraemonkit.kit.test

import com.didichuxing.doraemonkit.kit.test.utils.XposedHookUtil


/**
 * didi Create on 2022/4/14 .
 *
 * Copyright (c) 2022/4/14 by didiglobal.com.
 *
 * @author <a href="realonlyone@126.com">zhangjun</a>
 * @version 1.0
 * @Date 2022/4/14 10:52 上午
 * @Description 测试功能管理类，管理测试相关功能
 */

object DoKitTestManager {


    private var testMode: TestMode = TestMode.UNKNOWN
    private val onTestModeChangeListenerSet: MutableSet<OnTestModeChangeListener> = mutableSetOf()

    /**
     * 是否是主机模式
     */
    fun isHostMode(): Boolean {
        return testMode == TestMode.HOST
    }

    /**
     * 是否是从机模式
     */
    fun isClientMode(): Boolean {
        return testMode == TestMode.CLIENT
    }

    /**
     * 测试功能是否关闭
     */
    fun isClose(): Boolean {
        return testMode == TestMode.UNKNOWN
    }

    fun getTestMode(): TestMode {
        return testMode
    }

    /**
     * 开始测试功能
     * 1、开始hook 或者关闭hook
     */
    fun startTest(testMode: TestMode) {
        if (testMode == TestMode.HOST) {
            startTestByHostMode()
        } else if (testMode == TestMode.CLIENT) {
            startTestByClientMode()
        } else {
            closeTest()
        }
    }

    /**
     * 关闭测试功能
     * 备注：关闭后测试相关功能将不工作
     */
    fun closeTest() {
        testMode = TestMode.UNKNOWN
        if (XposedHookUtil.isRunTimeHookEnable()) {
            XposedHookUtil.stopRunTimeHook()
        }
    }

    private fun dispatchTestModeChange(testMode: TestMode) {
        onTestModeChangeListenerSet.forEach {
            try {
                it.onTestModeChanged(testMode)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun addOnTestModeChangeListener(listener: OnTestModeChangeListener) {
        onTestModeChangeListenerSet.add(listener)
    }


    fun removeOnTestModeChangeListener(listener: OnTestModeChangeListener) {
        onTestModeChangeListenerSet.remove(listener)
    }

    private fun startTestByHostMode() {
        testMode = TestMode.HOST
        if (!XposedHookUtil.isRunTimeHookEnable()) {
            XposedHookUtil.startRunTimeHook()
        }
    }

    private fun startTestByClientMode() {
        testMode = TestMode.CLIENT
        if (XposedHookUtil.isRunTimeHookEnable()) {
            XposedHookUtil.stopRunTimeHook()
        }
    }


}
