package com.didichuxing.doraemonkit.kit.performance

import android.content.Context
import android.view.View
import android.widget.FrameLayout
import com.didichuxing.doraemonkit.kit.core.AbsDokitView
import com.didichuxing.doraemonkit.kit.core.DokitViewLayoutParams


/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2019-10-11-16:05
 * 描    述：性能监控 帧率、 CPU、RAM、流量监控统一显示的DokitView 功能待实现
 * 修订历史：
 * ================================================
 */
class PerformanceDokitView : AbsDokitView() {
    override fun onCreate(context: Context?) {
    }

    override fun onCreateView(context: Context?, rootView: FrameLayout?): View {
        return rootView!!
    }

    override fun onViewCreated(rootView: FrameLayout?) {
    }

    override fun initDokitViewLayoutParams(params: DokitViewLayoutParams?) {

    }


}