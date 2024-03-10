package com.didichuxing.doraemonkit.kit.autotest.ui

import android.os.Bundle
import android.view.View
import com.didichuxing.doraemonkit.autotest.R
import com.didichuxing.doraemonkit.kit.autotest.AutoTestManager
import com.didichuxing.doraemonkit.kit.core.BaseFragment
import com.didichuxing.doraemonkit.kit.test.TestMode
import com.didichuxing.doraemonkit.kit.test.report.ScreenShotManager
import com.didichuxing.doraemonkit.util.ToastUtils


/**
 * didi Create on 2022/4/14 .
 *
 * Copyright (c) 2022/4/14 by didiglobal.com.
 *
 * @author <a href="realonlyone@126.com">zhangjun</a>
 * @version 1.0
 * @Date 2022/4/14 4:40 下午
 * @Description 用一句话说明文件功能
 */

class DoKitAutotestFragment : BaseFragment() {


    override fun onRequestLayout(): Int {
        return R.layout.dk_fragment_autotest_main
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<View>(R.id.connect).setOnClickListener {
            starConnect()
        }
        view.findViewById<View>(R.id.record).setOnClickListener {
            startRecording()
        }
        view.findViewById<View>(R.id.record_stop).setOnClickListener {
            stopRecording()
        }
        view.findViewById<View>(R.id.test).setOnClickListener {
            startTest()
        }
        view.findViewById<View>(R.id.test_stop).setOnClickListener {
            stopTest()
        }
        view.findViewById<View>(R.id.caseList).setOnClickListener {
            ToastUtils.showShort("不支持")
        }
    }

    private var screenShotManager: ScreenShotManager = ScreenShotManager("doKit/autotest/screen2")

    private fun test() {
        val bitmap = screenShotManager.screenshotBitmap(activity)
    }

    private fun starConnect() {
        //使用统一的链接管理
        if (activity is DoKitAutotestActivity) {
            (activity as DoKitAutotestActivity).pushFragment(AutotestPage.CONNECT)
        }
    }

    private fun startRecording() {
        when (AutoTestManager.getMode()) {
            TestMode.UNKNOWN -> {
                AutoTestManager.startRecord()
            }
            TestMode.HOST -> {
                ToastUtils.showShort("已经在录制中")
            }
            TestMode.CLIENT -> {
                ToastUtils.showShort("在测试中，请先关闭")
            }
        }

    }

    private fun stopRecording() {
        when (AutoTestManager.getMode()) {
            TestMode.HOST -> {
                AutoTestManager.stopRecord()
            }
            else -> {
                ToastUtils.showShort("不在录制中")
            }
        }
    }

    private fun startTest() {
        when (AutoTestManager.getMode()) {
            TestMode.UNKNOWN -> {
                AutoTestManager.startAutoTest()
            }
            TestMode.HOST -> {
                ToastUtils.showShort("在录制中，请先关闭")
            }
            TestMode.CLIENT -> {
                ToastUtils.showShort("已经在测试中")
            }
        }
    }

    private fun stopTest() {
        when (AutoTestManager.getMode()) {
            TestMode.CLIENT -> {
                AutoTestManager.stopAutoTest()
            }
            else -> {
                ToastUtils.showShort("不在测试中")
            }
        }
    }


}
