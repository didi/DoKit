package com.didichuxing.doraemonkit.kit.uiperformance

import android.content.Context
import com.didichuxing.doraemonkit.R
import com.didichuxing.doraemonkit.kit.AbstractKit
import com.didichuxing.doraemonkit.kit.core.DokitIntent
import com.didichuxing.doraemonkit.kit.core.DokitViewManager

/**
 * Created by wanglikun on 2019-06-27
 * UI渲染性能kit
 */
class UIPerformanceKit : AbstractKit() {
    override val name: Int
        get() = R.string.dk_kit_ui_performance

    override val icon: Int
        get() = R.mipmap.dk_ui_performance

    override fun onClick(context: Context?) {
        UIPerformanceManager.instance.startTrack()

        DokitViewManager.instance.detachToolPanel()
        val intent = DokitIntent(UIPerformanceDisplayDokitView::class.java)
        intent.mode = DokitIntent.MODE_SINGLE_INSTANCE
        DokitViewManager.instance.attach(intent)

        val intentInfo = DokitIntent(UIPerformanceInfoDokitView::class.java)
        intentInfo.mode = DokitIntent.MODE_SINGLE_INSTANCE
        DokitViewManager.instance.attach(intentInfo)
        //直接显示层级
        UIPerformanceManager.instance.initRefresh()
    }

    override fun onAppInit(context: Context?) {}
    override val isInnerKit: Boolean
        get() = true

    override fun innerKitId(): String {
        return "dokit_sdk_performance_ck_hierarchy"
    }
}