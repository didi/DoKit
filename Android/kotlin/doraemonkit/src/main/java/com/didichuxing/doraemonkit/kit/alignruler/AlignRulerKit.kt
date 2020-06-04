package com.didichuxing.doraemonkit.kit.alignruler

import android.content.Context
import com.didichuxing.doraemonkit.R
import com.didichuxing.doraemonkit.config.AlignRulerConfig
import com.didichuxing.doraemonkit.kit.AbstractKit
import com.didichuxing.doraemonkit.kit.core.DokitIntent
import com.didichuxing.doraemonkit.kit.core.DokitViewManager

/**
 * Created by wanglikun on 2018/9/19.
 */
class AlignRulerKit : AbstractKit() {
    override val name: Int
        get() = R.string.dk_kit_align_ruler

    override val icon: Int
        get() = R.mipmap.dk_align_ruler

    override fun onClick(context: Context?) {
        DokitViewManager.instance.detachToolPanel()
        var pageIntent = DokitIntent(AlignRulerMarkerDokitView::class.java)
        pageIntent.mode = DokitIntent.MODE_SINGLE_INSTANCE
        DokitViewManager.instance.attach(pageIntent)

        pageIntent = DokitIntent(AlignRulerLineDokitView::class.java)
        pageIntent.mode = DokitIntent.MODE_SINGLE_INSTANCE
        DokitViewManager.instance.attach(pageIntent)

        pageIntent = DokitIntent(AlignRulerInfoDokitView::class.java)
        pageIntent.mode = DokitIntent.MODE_SINGLE_INSTANCE
        DokitViewManager.instance.attach(pageIntent)

        AlignRulerConfig.isAlignRulerOpen = true
    }

    override fun onAppInit(context: Context?) {
        AlignRulerConfig.isAlignRulerOpen = false
    }

    override val isInnerKit: Boolean
        get() = true

    override fun innerKitId(): String {
        return "dokit_sdk_ui_ck_aligin_scaleplate"
    }
}