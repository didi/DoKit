package com.didichuxing.doraemonkit.kit.h5_help

import android.content.Context
import com.didichuxing.doraemonkit.R
import com.didichuxing.doraemonkit.kit.AbstractKit
import com.didichuxing.doraemonkit.kit.core.DokitIntent
import com.didichuxing.doraemonkit.kit.core.DokitViewManager
import com.didichuxing.doraemonkit.kit.loginfo.LogInfoDokitView

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020/8/24-20:41
 * 描    述：
 * 修订历史：
 * ================================================
 */
class H5Kit : AbstractKit() {
    override val name: Int
        get() = R.string.dk_kit_h5_help

    override val icon: Int
        get() = R.mipmap.dk_icon_h5help

    override val isInnerKit: Boolean
        get() = true

    override fun onClick(context: Context?) {
        val intent = DokitIntent(H5DokitView::class.java)
        intent.mode = DokitIntent.MODE_SINGLE_INSTANCE
        DokitViewManager.getInstance().attach(intent)
    }

    override fun onAppInit(context: Context?) {
    }

    override fun innerKitId(): String {
        return "dokit_sdk_comm_ck_h5"
    }
}