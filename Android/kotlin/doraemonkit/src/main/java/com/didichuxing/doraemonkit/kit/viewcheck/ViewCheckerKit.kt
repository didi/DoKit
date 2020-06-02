package com.didichuxing.doraemonkit.kit.viewcheck

import android.content.Context
import com.didichuxing.doraemonkit.R
import com.didichuxing.doraemonkit.kit.AbstractKit
import com.didichuxing.doraemonkit.kit.core.DokitIntent
import com.didichuxing.doraemonkit.kit.core.DokitViewManager

/**
 * Created by wanglikun on 2018/11/20.
 */
class ViewCheckerKit : AbstractKit() {
    override val name: Int
        get() = R.string.dk_kit_view_check

    override val icon: Int
        get() = R.mipmap.dk_view_check

    override fun onClick(context: Context?) {
        kotlinTip()
    }

    override fun onAppInit(context: Context?) {}
    override val isInnerKit: Boolean
        get() = true

    override fun innerKitId(): String {
        return "dokit_sdk_ui_ck_widget"
    }
}