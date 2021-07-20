package com.didichuxing.doraemonkit.kit.sysinfo

import android.app.Activity
import android.content.Context
import com.didichuxing.doraemonkit.R
import com.didichuxing.doraemonkit.kit.AbstractKit
import com.didichuxing.doraemonkit.util.DoKitSystemUtil
import com.google.auto.service.AutoService

/**
 * 进入本地语言设置页面
 * Created by jint on 2018/6/22.
 */
@AutoService(AbstractKit::class)
class LocalLangKit : AbstractKit() {
    override val name: Int
        get() = R.string.dk_kit_local_lang
    override val icon: Int
        get() = R.mipmap.dk_kit_local_lang

    override fun onClickWithReturn(activity: Activity): Boolean {
        DoKitSystemUtil.startLocalActivity(activity)
        return true
    }

    override fun onAppInit(context: Context?) {}
    override val isInnerKit: Boolean
        get() = true

    override fun innerKitId(): String {
        return "dokit_sdk_comm_ck_local_lang"
    }
}