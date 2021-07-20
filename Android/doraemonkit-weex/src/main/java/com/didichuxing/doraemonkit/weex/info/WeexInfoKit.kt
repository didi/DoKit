package com.didichuxing.doraemonkit.weex.info

import android.app.Activity
import android.content.Context
import com.didichuxing.doraemonkit.kit.AbstractKit
import com.didichuxing.doraemonkit.kit.parameter.cpu.CpuMainPageFragment
import com.didichuxing.doraemonkit.weex.R
import com.google.auto.service.AutoService

/**
 * @author haojianglong
 * @date 2019-06-11
 */
@AutoService(AbstractKit::class)
class WeexInfoKit : AbstractKit() {
    override val name: Int
        get() = R.string.dk_weex_info_name
    override val icon: Int
        get() = R.mipmap.dk_sys_info

    override fun onClickWithReturn(activity: Activity): Boolean {
        startUniversalActivity(CpuMainPageFragment::class.java, activity, null, true)
        return true
    }

    override fun onAppInit(context: Context?) {}
    override val isInnerKit: Boolean
        get() = true

    override fun innerKitId(): String {
        return "dokit_sdk_weex_ck_info"
    }
}