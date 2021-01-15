package com.didichuxing.doraemondemo.dokit

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.CompoundButton
import android.widget.FrameLayout
import com.didichuxing.doraemondemo.R
import com.didichuxing.doraemonkit.kit.core.SimpleDokitStarter
import com.didichuxing.doraemonkit.kit.core.SimpleDokitView
import com.didichuxing.doraemonkit.kit.core.ViewSetupHelper

/**
 * @Author: changzuozhen
 * @Date: 2020-12-22
 * 切换全屏与否只需要调整继承关系即可
 * @see TestSimpleDokitFragment
 * @see TestSimpleDokitFloatView
 *
 * 悬浮窗，支持折叠
 * @see com.didichuxing.doraemonkit.kit.core.SimpleDokitView
 * 启动工具函数
 * @see com.didichuxing.doraemonkit.kit.core.SimpleDokitStarter.startFloating
 *
 * 全屏页面
 * @see com.didichuxing.doraemonkit.kit.core.SimpleDokitFragment
 * 启动工具函数
 * @see com.didichuxing.doraemonkit.kit.core.SimpleDokitStarter.startFullScreen(java.lang.Class<? extends com.didichuxing.doraemonkit.kit.core.SimpleDokitFragment>, android.content.Context, android.os.Bundle)
 *
 */
class TestSimpleDokitFloatView : SimpleDokitView() {
    override fun getLayoutId(): Int {
        return R.layout.layout_demo_custom
    }

    override fun onViewCreated(rootView: FrameLayout?) {
        super.onViewCreated(rootView)

        ViewSetupHelper.setupButton(rootView, R.id.test1, "TestSimpleDokitFragment", View.OnClickListener {
            val bundle = Bundle()
            bundle.putString("test", "test")
            SimpleDokitStarter.startFullScreen(TestSimpleDokitFragment::class.java, context)
        })

        // 隐藏
        ViewSetupHelper.setupButton(rootView, R.id.test2, "", null)

        ViewSetupHelper.setupToggleButton(rootView, R.id.tb_test1, "TB", false, CompoundButton.OnCheckedChangeListener { buttonView, isChecked -> Log.d("TEST", "TB $isChecked") })
        // 隐藏
        ViewSetupHelper.setupToggleButton(rootView, R.id.tb_test2, "", false, null)

    }
}