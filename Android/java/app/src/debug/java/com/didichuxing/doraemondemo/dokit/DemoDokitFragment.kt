package com.didichuxing.doraemondemo.dokit

import android.os.Bundle
import android.view.View
import android.widget.CompoundButton
import com.didichuxing.doraemondemo.R
import com.didichuxing.doraemonkit.kit.core.AbsDokitFragment
import com.didichuxing.doraemonkit.kit.core.SimpleDokitStarter.startFullScreen
import com.didichuxing.doraemonkit.kit.core.ViewSetupHelper

/**
 * @Author: changzuozhen
 * @Date: 2020-12-22
 * 切换全屏与否只需要调整继承关系即可
 * @see DemoDokitFragment
 *
 * @see TestSimpleDokitFloatView
 *
 *
 * 悬浮窗，支持折叠
 *
 * @see com.didichuxing.doraemonkit.kit.core.SimpleDokitView
 * 启动工具函数
 *
 * @see com.didichuxing.doraemonkit.kit.core.SimpleDokitStarter.startFloating
 *
 *
 * 全屏页面
 *
 * @see com.didichuxing.doraemonkit.kit.core.AbsDokitFragment
 * 启动工具函数
 *
 * @see com.didichuxing.doraemonkit.kit.core.SimpleDokitStarter.startFullScreen
 */
class DemoDokitFragment : AbsDokitFragment() {
    override fun onViewCreated(rootView: View?) {
        super.onViewCreated(view)
        ViewSetupHelper.setupButton(rootView, R.id.test1, "TestSimpleDokitFragment") { v: View? ->
            val bundle = Bundle()
            bundle.putString("test", "test")
            startFullScreen(DemoDokitFragment::class.java, context)
        }

        // 隐藏
        ViewSetupHelper.setupButton(rootView, R.id.test2, "") { v: View? -> }
        ViewSetupHelper.setupToggleButton(
            rootView,
            R.id.tb_test1,
            "TB",
            false
        ) { buttonView: CompoundButton?, isChecked: Boolean -> }

        // 隐藏
        ViewSetupHelper.setupToggleButton(
            rootView,
            R.id.tb_test2,
            "",
            false
        ) { buttonView: CompoundButton?, isChecked: Boolean -> }
    }


    override fun layoutId(): Int {
        return R.layout.layout_demo_custom
    }

    override fun initTitle(): String {
        return "我是自定义页面"
    }
}