package com.didichuxing.doraemondemo.dokit

import android.os.Bundle
import android.view.View
import android.widget.CompoundButton
import com.didichuxing.doraemondemo.R
import com.didichuxing.doraemonkit.DoKit
import com.didichuxing.doraemonkit.kit.core.AbsDoKitFragment

/**
 * @Author: changzuozhen
 * @Date: 2020-12-22
 * 切换全屏与否只需要调整继承关系即可
 * @see CustomDokitFragment
 *
 * @see TestSimpleDoKitFloatView
 *
 *
 * 悬浮窗，支持折叠
 *
 * 启动工具函数
 *
 *
 *
 * 全屏页面
 *
 * @see com.didichuxing.doraemonkit.kit.core.AbsDoKitFragment
 * 启动工具函数
 *
 */
class CustomDokitFragment : AbsDoKitFragment() {
    override fun onViewCreated(rootView: View?) {
        super.onViewCreated(view)
        ViewSetupHelper.setupButton(rootView, R.id.test1, "TestSimpleDokitFragment") { v: View? ->
            val bundle = Bundle()
            bundle.putString("test", "test")
            DoKit.launchFullScreen(CustomDokitFragment::class.java, context, isSystemFragment = false)
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
