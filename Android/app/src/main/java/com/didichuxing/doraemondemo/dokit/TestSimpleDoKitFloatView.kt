package com.didichuxing.doraemondemo.dokit

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.CompoundButton
import android.widget.FrameLayout
import com.didichuxing.doraemondemo.R
import com.didichuxing.doraemonkit.DoKit

/**
 * @Author: changzuozhen
 * @Date: 2020-12-22
 * 切换全屏与否只需要调整继承关系即可
 * @see CustomDokitFragment
 * @see TestSimpleDoKitFloatView
 *
 * 悬浮窗，支持折叠
 * @see com.didichuxing.doraemonkit.kit.core.SimpleDokitView
 * 启动工具函数
 *
 * 全屏页面
 * @see com.didichuxing.doraemonkit.kit.core.AbsDoKitFragment
 * 启动工具函数
 *
 */
class TestSimpleDoKitFloatView : SimpleDoKitView() {
    override fun getLayoutId(): Int {
        return R.layout.layout_demo_custom
    }

    override fun onViewCreated(rootView: FrameLayout?) {
        super.onViewCreated(rootView)

        ViewSetupHelper.setupButton(rootView, R.id.test1, "TestSimpleDokitFragment", View.OnClickListener {
            val bundle = Bundle()
            bundle.putString("test", "test")
            DoKit.launchFullScreen(CustomDokitFragment::class.java, context)
        })

        // 隐藏
        ViewSetupHelper.setupButton(rootView, R.id.test2, "", null)

        ViewSetupHelper.setupToggleButton(rootView, R.id.tb_test1, "TB", false, CompoundButton.OnCheckedChangeListener { buttonView, isChecked -> Log.d("TEST", "TB $isChecked") })
        // 隐藏
        ViewSetupHelper.setupToggleButton(rootView, R.id.tb_test2, "", false, null)

    }


}
