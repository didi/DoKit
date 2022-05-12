package com.didichuxing.doraemonkit.kit.autotest

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.didichuxing.doraemonkit.aop.DokitPluginConfig
import com.didichuxing.doraemonkit.autotest.R
import com.didichuxing.doraemonkit.kit.AbstractKit
import com.didichuxing.doraemonkit.kit.autotest.ui.DoKitAutotestActivity
import com.didichuxing.doraemonkit.util.DoKitCommUtil
import com.didichuxing.doraemonkit.util.ToastUtils
import com.google.auto.service.AutoService


/**
 * didi Create on 2022/4/6 .
 *
 * Copyright (c) 2022/4/6 by didiglobal.com.
 *
 * @author <a href="realonlyone@126.com">zhangjun</a>
 * @version 1.0
 * @Date 2022/4/6 5:14 下午
 * @Description 用一句话说明文件功能
 */

@AutoService(AbstractKit::class)
class AutoTestControlKit : AbstractKit() {

    override val name: Int
        get() = R.string.dk_kit_autotest
    override val icon: Int
        get() = R.drawable.dk_icon_autotest


    override fun onClickWithReturn(activity: Activity): Boolean {
        if (!DokitPluginConfig.SWITCH_DOKIT_PLUGIN) {
            ToastUtils.showShort(DoKitCommUtil.getString(R.string.dk_plugin_close_tip))
            return false
        }
        val intent = Intent(activity, DoKitAutotestActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        activity.startActivity(intent)

        return true
    }

    override fun onAppInit(context: Context?) {

    }

    override val isInnerKit: Boolean
        get() = true

    override fun innerKitId(): String {
        return "dokit_sdk_platform_ck_autotest"
    }
}
