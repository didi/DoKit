package com.didichuxing.doraemonkit.kit.layoutborder

import android.app.Activity
import android.content.Context
import com.didichuxing.doraemonkit.DoKit
import com.didichuxing.doraemonkit.R
import com.didichuxing.doraemonkit.config.LayoutBorderConfig
import com.didichuxing.doraemonkit.kit.AbstractKit
import com.google.auto.service.AutoService

/**
 * Created by wanglikun on 2019/1/7
 */
@AutoService(AbstractKit::class)
class LayoutBorderKit : AbstractKit() {
    override val name: Int
        get() = R.string.dk_kit_layout_border
    override val icon: Int
        get() = R.mipmap.dk_view_border

    override fun onClickWithReturn(activity: Activity): Boolean {
        DoKit.launchFloating(LayoutLevelDoKitView::class.java)
        LayoutBorderManager.getInstance().start()
        LayoutBorderConfig.setLayoutBorderOpen(true)
        LayoutBorderConfig.setLayoutLevelOpen(true)
        return true
    }

    override fun onAppInit(context: Context?) {
        LayoutBorderConfig.setLayoutBorderOpen(false)
        LayoutBorderConfig.setLayoutLevelOpen(false)
    }

    override val isInnerKit: Boolean
        get() = true

    override fun innerKitId(): String = "dokit_sdk_ui_ck_border"
}
