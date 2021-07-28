package com.didichuxing.doraemonkit.kit.sysinfo

import android.app.Activity
import android.content.Context
import com.didichuxing.doraemonkit.R
import com.didichuxing.doraemonkit.kit.AbstractKit
import com.google.auto.service.AutoService

/**
 * 设备、app信息
 * Created by zhangweida on 2018/6/22.
 */
@AutoService(AbstractKit::class)
class SysInfoKit : AbstractKit() {
    override val name: Int
        get() = R.string.dk_kit_sysinfo
    override val icon: Int
        get() = R.mipmap.dk_sys_info

    override fun onClickWithReturn(activity: Activity): Boolean {
        startUniversalActivity(SysInfoFragment::class.java, activity, null, true)
        return true
    }

    override fun onAppInit(context: Context?) {}
    override val isInnerKit: Boolean
        get() = true

    override fun innerKitId(): String {
        return "dokit_sdk_comm_ck_appinfo"
    }
}