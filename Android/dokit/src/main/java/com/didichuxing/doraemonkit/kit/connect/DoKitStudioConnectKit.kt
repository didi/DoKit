package com.didichuxing.doraemonkit.kit.connect

import android.app.Activity
import android.content.Context
import com.didichuxing.doraemonkit.R
import com.didichuxing.doraemonkit.aop.DokitPluginConfig
import com.didichuxing.doraemonkit.kit.AbstractKit
import com.didichuxing.doraemonkit.util.DoKitCommUtil
import com.didichuxing.doraemonkit.util.ToastUtils
import com.google.auto.service.AutoService

/**
 * didi Create on 2022/4/12 .
 *
 * Copyright (c) 2022/4/12 by didiglobal.com.
 *
 * @author <a href="realonlyone@126.com">zhangjun</a>
 * @version 1.0
 * @Date 2022/4/12 6:07 下午
 * @Description 用一句话说明文件功能
 */
@AutoService(AbstractKit::class)
class DoKitStudioConnectKit : AbstractKit() {
    override val name: Int
        get() = R.string.dk_kit_dokit_studio
    override val icon: Int
        get() = R.mipmap.dk_dokit_for_web

    override fun onClickWithReturn(activity: Activity): Boolean {
        if (!DokitPluginConfig.SWITCH_DOKIT_PLUGIN) {
            ToastUtils.showShort(DoKitCommUtil.getString(R.string.dk_plugin_close_tip))
            return false
        }
        startUniversalActivity(DoKitConnectFragment::class.java, activity, null, true)
        return true
    }

    override fun onAppInit(context: Context?) {

    }
    override val isInnerKit: Boolean
        get() = true

    override fun innerKitId(): String {
        return "dokit_sdk_platform_ck_dokit_connect"
    }
}
