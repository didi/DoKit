package com.didichuxing.doraemonkit.kit.alignruler

import android.content.Context
import com.didichuxing.doraemonkit.R
import com.didichuxing.doraemonkit.kit.AbstractKit

/**
 * Created by wanglikun on 2018/9/19.
 */
class AlignRulerKit : AbstractKit() {
    override val name: Int
        get() = R.string.dk_kit_align_ruler

    override val icon: Int
        get() = R.mipmap.dk_align_ruler

    override fun onClick(context: Context?) {
        kotlinTip()
    }

    override fun onAppInit(context: Context?) {
    }

    override val isInnerKit: Boolean
        get() = true

    override fun innerKitId(): String {
        return "dokit_sdk_ui_ck_aligin_scaleplate"
    }
}