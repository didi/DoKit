package com.didichuxing.doraemonkit.kit.autotest.ui

import android.content.Context
import android.content.Intent
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.didichuxing.doraemonkit.DoKit
import com.didichuxing.doraemonkit.autotest.R
import com.didichuxing.doraemonkit.kit.autotest.AutoTestManager
import com.didichuxing.doraemonkit.kit.core.AbsDoKitView
import com.didichuxing.doraemonkit.kit.core.DoKitViewLayoutParams
import com.didichuxing.doraemonkit.kit.test.TestMode
import com.didichuxing.doraemonkit.kit.test.widget.FlashImageView
import com.didichuxing.doraemonkit.kit.test.widget.FlashTextView
import com.didichuxing.doraemonkit.util.ActivityUtils
import com.didichuxing.doraemonkit.util.ConvertUtils
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

class RecordingCaseDoKitView : AbsDoKitView() {

    companion object {
        val doKitViews: MutableSet<RecordingCaseDoKitView> = mutableSetOf()

        fun changeText(text: String) {
            doKitViews.forEach {
                it.changeText(text)
            }
        }

        fun changeDotColor(id: Int) {
            doKitViews.forEach {
                it.changeDotColor(id)
            }
        }

        fun changeMode(mode: TestMode) {
            doKitViews.forEach {
                it.changeMode(mode)
            }
        }
    }

    private var mRedDot: FlashImageView? = null
    private var mExtend: FlashTextView? = null
    private var mText: TextView? = null


    override fun onCreate(context: Context?) {

    }

    override fun onCreateView(context: Context?, rootView: FrameLayout?): View {
        return LayoutInflater.from(context).inflate(R.layout.dk_autotest_view_recording_case, rootView, false)
    }

    override fun onViewCreated(rootView: FrameLayout?) {

        mRedDot = findViewById(R.id.dot)
        mExtend = findViewById(R.id.tv_extend)
        mText = findViewById(R.id.tv_text)

        rootView?.setOnClickListener {
            if (ActivityUtils.getTopActivity() is DoKitAutotestActivity) {
                return@setOnClickListener
            }
            val intent = Intent(activity, DoKitAutotestActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            activity.startActivity(intent)
        }
        rootView?.findViewById<ImageView>(R.id.iv_close)?.setOnClickListener {

            if (AutoTestManager.getMode() == TestMode.UNKNOWN) {
                AutoTestManager.stopConnect()
                DoKit.removeFloating(RecordingCaseDoKitView::class)
            } else {
                ToastUtils.showShort("正在测试或录制，请先停止")
            }

        }

        mRedDot?.startFlash()
        mExtend?.startFlash()

        changeMode(AutoTestManager.getMode())

        doKitViews.add(this)

    }

    private fun changeText(text: String) {
        mText?.text = text
    }

    private fun changeDotColor(id: Int) {
        mRedDot?.setBackgroundResource(id)
    }

    fun changeMode(mode: TestMode) {
        var dotColor = R.drawable.dk_autotest_flash_red_bg
        var text = "待链接"
        when (mode) {
            TestMode.UNKNOWN -> {
                dotColor = R.drawable.dk_autotest_flash_red_bg
                text = "已链接"
            }
            TestMode.HOST -> {
                dotColor = R.drawable.dk_autotest_flash_green_bg
                text = "录制中"
            }
            TestMode.CLIENT -> {
                dotColor = R.drawable.dk_autotest_flash_blue_bg
                text = "测试中"
            }
        }
        changeText(text)
        changeDotColor(dotColor)
    }

    override fun initDokitViewLayoutParams(params: DoKitViewLayoutParams) {
        params.width = DoKitViewLayoutParams.WRAP_CONTENT
        params.height = DoKitViewLayoutParams.WRAP_CONTENT
        params.gravity = Gravity.TOP or Gravity.LEFT
        params.x = ConvertUtils.dp2px(25f)
        params.y = ConvertUtils.dp2px(25f)
    }

    override fun onDestroy() {
        super.onDestroy()
        mRedDot?.cancelFlash()
        mExtend?.cancelFlash()

        doKitViews.remove(this)
    }
}
