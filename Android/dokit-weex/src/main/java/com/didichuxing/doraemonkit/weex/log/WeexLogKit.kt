package com.didichuxing.doraemonkit.weex.log

import android.app.Activity
import android.content.Context
import com.didichuxing.doraemonkit.DoKit
import com.didichuxing.doraemonkit.kit.AbstractKit
import com.didichuxing.doraemonkit.kit.loginfo.LogInfoManager
import com.didichuxing.doraemonkit.weex.R
import com.google.auto.service.AutoService

/**
 * @author haojianglong
 * @date 2019-06-11
 */
@AutoService(AbstractKit::class)
class WeexLogKit : AbstractKit() {
    override val name: Int
        get() = R.string.dk_console_log_name
    override val icon: Int
        get() = R.mipmap.dk_log_info

    override fun onClickWithReturn(activity: Activity): Boolean {
        DoKit.launchFloating<WeexLogInfoDoKitView>()

        //开启日志服务
        LogInfoManager.getInstance().start()
        return true
    }

    override fun onAppInit(context: Context?) {}
    override val isInnerKit: Boolean
        get() = true

    override fun innerKitId(): String {
        return "dokit_sdk_weex_ck_log"
    }
}
