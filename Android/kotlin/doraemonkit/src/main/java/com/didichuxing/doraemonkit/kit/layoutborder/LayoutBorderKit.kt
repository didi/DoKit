package com.didichuxing.doraemonkit.kit.layoutborder

import android.content.Context
import com.didichuxing.doraemonkit.R
import com.didichuxing.doraemonkit.kit.AbstractKit
import com.didichuxing.doraemonkit.kit.core.DokitViewManager

/**
 * Created by wanglikun on 2019/1/7
 */
class LayoutBorderKit : AbstractKit() {
    override val name: Int
        get() = R.string.dk_kit_layout_border

    override val icon: Int
        get() = R.mipmap.dk_view_border

    override fun onClick(context: Context?) {
        DokitViewManager.instance.detachToolPanel()
        LayoutManager.instance.showBorder()
    }

    override fun onAppInit(context: Context?) {
    }

    override val isInnerKit: Boolean
        get() = true

    override fun innerKitId(): String {
        return "dokit_sdk_ui_ck_border"
    }
}