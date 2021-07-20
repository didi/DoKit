package com.didichuxing.doraemonkit.kit.loginfo

import android.app.Activity
import android.content.Context
import com.didichuxing.doraemonkit.DoKit.Companion.launchFloating
import com.didichuxing.doraemonkit.R
import com.didichuxing.doraemonkit.config.LogInfoConfig
import com.didichuxing.doraemonkit.kit.AbstractKit
import com.google.auto.service.AutoService

/**
 * Created by wanglikun on 2018/10/9.
 */
@AutoService(AbstractKit::class)
class LogInfoKit : AbstractKit() {
    override val name: Int
        get() = R.string.dk_kit_log_info
    override val icon: Int
        get() = R.mipmap.dk_log_info

    override fun onClickWithReturn(activity: Activity): Boolean {
        launchFloating(LogInfoDokitView::class.java)
        //开启日志服务
        LogInfoManager.getInstance().start()
        return true
    }

    override fun onAppInit(context: Context?) {
        LogInfoConfig.setLogInfoOpen(false)
    }

    override val isInnerKit: Boolean
        get() = true

    override fun innerKitId(): String {
        return "dokit_sdk_comm_ck_log"
    }
}