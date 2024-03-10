package com.didichuxing.doraemonkit.kit.uiperformance

import android.app.Activity
import android.content.Context
import com.didichuxing.doraemonkit.DoKit
import com.didichuxing.doraemonkit.R
import com.didichuxing.doraemonkit.kit.AbstractKit
import com.google.auto.service.AutoService

/**
 * Created by wanglikun on 2019-06-27
 * UI渲染性能kit
 */
@AutoService(AbstractKit::class)
class UIPerformanceKit : AbstractKit() {
    override val name: Int
        get() = R.string.dk_kit_ui_performance
    override val icon: Int
        get() = R.mipmap.dk_ui_performance

    override val isInnerKit: Boolean
        get() = true

    override fun onClickWithReturn(activity: Activity): Boolean {
        UIPerformanceManager.getInstance().start(activity)
        DoKit.launchFloating<UIPerformanceDisplayDoKitView>()
        DoKit.launchFloating<UIPerformanceInfoDoKitView>()

        //直接显示层级
        UIPerformanceManager.getInstance().initRefresh()
        return true
    }

    override fun onAppInit(context: Context?) {}

    override fun innerKitId(): String = "dokit_sdk_performance_ck_hierarchy"
}
