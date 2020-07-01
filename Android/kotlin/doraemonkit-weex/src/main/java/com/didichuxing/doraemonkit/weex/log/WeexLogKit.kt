package com.didichuxing.doraemonkit.weex.log

import android.content.Context
import com.didichuxing.doraemonkit.kit.AbstractKit
import com.didichuxing.doraemonkit.kit.core.DokitIntent
import com.didichuxing.doraemonkit.kit.core.DokitViewManager
import com.didichuxing.doraemonkit.kit.loginfo.LogInfoManager
import com.didichuxing.doraemonkit.weex.R
import com.didichuxing.doraemonkit.weex.log.widget.WeexLogInfoDokitView

/**
 * Transformed by alvince on 2020/6/30
 *
 * @author haojianglong
 * @date 2019-06-11
 */
class WeexLogKit : AbstractKit() {

    override val name: Int
        get() = R.string.dk_console_log_name

    override val icon: Int
        get() = R.mipmap.dk_log_info

    override val isInnerKit: Boolean
        get() = true

    override fun onAppInit(context: Context?) {
    }

    override fun onClick(context: Context?) {
        DokitIntent(WeexLogInfoDokitView::class.java)
            .apply {
                mode = DokitIntent.MODE_SINGLE_INSTANCE
            }
            .also { intent ->
                DokitViewManager.instance.attach(intent)
            }
        //开启日志服务
        LogInfoManager.start()
    }

    override fun innerKitId(): String = "dokit_sdk_weex_ck_log"

}