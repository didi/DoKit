package com.didichuxing.doraemonkit.kit.h5_help

import android.app.Activity
import android.content.Context
import com.didichuxing.doraemonkit.DoKit
import com.didichuxing.doraemonkit.R
import com.didichuxing.doraemonkit.kit.AbstractKit
import com.google.auto.service.AutoService

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020/8/24-20:41
 * 描    述：
 * 修订历史：
 * ================================================
 */
@AutoService(AbstractKit::class)
class H5Kit : AbstractKit() {
    override val name: Int
        get() = R.string.dk_kit_h5_help

    override val icon: Int
        get() = R.mipmap.dk_icon_h5help

    override val isInnerKit: Boolean
        get() = true

    override fun onClickWithReturn(activity: Activity): Boolean {
        DoKit.launchFloating(H5DoKitView::class)
        return true
    }

    override fun onAppInit(context: Context?) {
    }

    override fun innerKitId(): String {
        return "dokit_sdk_comm_ck_h5kit"
    }
}
