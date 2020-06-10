package com.didichuxing.doraemonkit.kit.sysinfo

import android.content.Context
import com.didichuxing.doraemonkit.R
import com.didichuxing.doraemonkit.constant.FragmentIndex
import com.didichuxing.doraemonkit.kit.AbstractKit

/**
 * 设备、app信息
 * Created by zhangweida on 2018/6/22.
 */
class SysInfoKit : AbstractKit() {
    override val name: Int
        get() = R.string.dk_kit_sysinfo

    override val icon: Int
        get() = R.mipmap.dk_sys_info

    override fun onClick(context: Context?) {
        startUniversalActivity(context, FragmentIndex.FRAGMENT_SYS_INFO)
    }

    override fun onAppInit(context: Context?) {}
    override val isInnerKit: Boolean
        get() = true

    override fun innerKitId(): String {
        return "dokit_sdk_comm_ck_appinfo"
    }
}