package com.didichuxing.doraemonkit.kit.loginfo

import android.content.Context
import com.didichuxing.doraemonkit.R
import com.didichuxing.doraemonkit.kit.AbstractKit
import com.didichuxing.doraemonkit.kit.core.DokitIntent
import com.didichuxing.doraemonkit.kit.core.DokitViewManager

/**
 * Created by wanglikun on 2018/10/9.
 */
class LogInfoKit : AbstractKit() {
    override val name: Int
        get() = R.string.dk_kit_log_info

    override val icon: Int
        get() = R.mipmap.dk_log_info

    override fun onClick(context: Context?) {
        val dokitIntent = DokitIntent(LogInfoDokitView::class.java).also {
            it.mode  = DokitIntent.MODE_SINGLE_INSTANCE
        }
        DokitViewManager.instance.attach(dokitIntent)
        LogInfoManager.start()
    }

    override fun onAppInit(context: Context?) {
    }

    override val isInnerKit: Boolean
        get() = true

    override fun innerKitId(): String {
        return "dokit_sdk_comm_ck_log"
    }
}