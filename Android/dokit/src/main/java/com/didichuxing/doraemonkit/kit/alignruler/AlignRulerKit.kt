package com.didichuxing.doraemonkit.kit.alignruler

import android.app.Activity
import android.content.Context
import com.didichuxing.doraemonkit.DoKit.Companion.getDoKitView
import com.didichuxing.doraemonkit.DoKit.Companion.launchFloating
import com.didichuxing.doraemonkit.R
import com.didichuxing.doraemonkit.config.AlignRulerConfig
import com.didichuxing.doraemonkit.kit.AbstractKit
import com.didichuxing.doraemonkit.util.ActivityUtils
import com.google.auto.service.AutoService

/**
 * Created by wanglikun on 2018/9/19.
 */
@AutoService(AbstractKit::class)
class AlignRulerKit : AbstractKit() {
    override val name: Int
        get() = R.string.dk_kit_align_ruler
    override val icon: Int
        get() = R.mipmap.dk_align_ruler

    override fun onClickWithReturn(activity: Activity): Boolean {
        launchFloating(AlignRulerMarkerDokitView::class.java)
        launchFloating(AlignRulerLineDokitView::class.java)
        launchFloating(AlignRulerInfoDokitView::class.java)
        val alignRulerInfoDokitView =
            getDoKitView(ActivityUtils.getTopActivity(), AlignRulerInfoDokitView::class.java)!!
        alignRulerInfoDokitView.setCheckBoxListener { isChecked ->
            val alignRulerLineDokitView =
                getDoKitView(ActivityUtils.getTopActivity(), AlignRulerLineDokitView::class.java)!!
            alignRulerLineDokitView.alignInfoView.refreshInfo(isChecked)
        }
        AlignRulerConfig.setAlignRulerOpen(true)
        return true
    }

    override fun onAppInit(context: Context?) {
        AlignRulerConfig.setAlignRulerOpen(false)
    }

    override val isInnerKit: Boolean
        get() = true

    override fun innerKitId(): String {
        return "dokit_sdk_ui_ck_aligin_scaleplate"
    }
}