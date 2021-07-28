package com.didichuxing.doraemonkit.kit.sysinfo

import android.app.Activity
import android.content.Context
import com.didichuxing.doraemonkit.R
import com.didichuxing.doraemonkit.aop.DokitPluginConfig
import com.didichuxing.doraemonkit.kit.AbstractKit
import com.didichuxing.doraemonkit.util.DoKitCommUtil
import com.didichuxing.doraemonkit.util.ToastUtils
import com.google.auto.service.AutoService

/**
 * 设备、app信息
 * Created by zhangweida on 2018/6/22.
 */
@AutoService(AbstractKit::class)
class ThirdLibInfoKit : AbstractKit() {
    override val name: Int
        get() = R.string.dk_third_library_info
    override val icon: Int
        get() = R.mipmap.dk_icon_third_lib

    override fun onClickWithReturn(activity: Activity): Boolean {
        if (!DokitPluginConfig.SWITCH_DOKIT_PLUGIN) {
            ToastUtils.showShort(DoKitCommUtil.getString(R.string.dk_plugin_close_tip))
            return false
        }
        startUniversalActivity(ThirdLibInfoFragment::class.java, activity, null, true)
        return true
    }

    override fun onAppInit(context: Context?) {}
    override val isInnerKit: Boolean
        get() = true

    override fun innerKitId(): String {
        return "dokit_sdk_comm_ck_thirdlibinfo"
    }
}