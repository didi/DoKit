package com.didichuxing.doraemonkit.kit.mc.ability

import com.didichuxing.doraemonkit.constant.DoKitConstant
import com.didichuxing.doraemonkit.constant.WSMode
import com.didichuxing.doraemonkit.kit.core.DokitAbility
import com.didichuxing.doraemonkit.kit.core.DokitIntent
import com.didichuxing.doraemonkit.kit.core.DokitViewManager
import com.didichuxing.doraemonkit.kit.core.SimpleDokitStarter
import com.didichuxing.doraemonkit.kit.mc.all.McConstant
import com.didichuxing.doraemonkit.kit.mc.client.ClientDokitView
import com.didichuxing.doraemonkit.kit.mc.server.HostDokitView
import com.didichuxing.doraemonkit.kit.mc.server.RecordingDokitView
import com.didichuxing.doraemonkit.util.LogHelper
import com.didichuxing.doraemonkit.util.SPUtils

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2021/6/7-19:50
 * 描    述：
 * 修订历史：
 * ================================================
 */
class DokitMcModuleProcessor : DokitAbility.DokitModuleProcessor {


    override fun values(): Map<String, Any> {
        return mapOf(
            "okhttp_interceptor" to DokitMcInterceptor(),
            "lifecycle" to McDokitLifecycleImpl()
        )
    }

    override fun proceed(actions: Map<String, Any>?): Map<String, Any> {
        actions?.let {
            when (actions["action"]) {
                "launch_host_view" -> {
                    val dokitIntent = DokitIntent(HostDokitView::class.java)
                    dokitIntent.mode = DokitIntent.MODE_ONCE
                    DokitViewManager.instance.attach(dokitIntent)
                }
                "launch_client_view" -> {
                    val dokitIntent = DokitIntent(ClientDokitView::class.java)
                    dokitIntent.mode = DokitIntent.MODE_ONCE
                    DokitViewManager.instance.attach(dokitIntent)
                }
                "launch_recoding_view" -> {
                    SimpleDokitStarter.startFloating(RecordingDokitView::class.java)
                    DoKitConstant.IS_MC_RECODING = true
                    McConstant.MC_CASE_ID =
                        SPUtils.getInstance().getString(DoKitConstant.MC_CASE_ID_KEY)
                    DoKitConstant.WS_MODE = WSMode.RECORDING

                    //添加录制中的悬浮窗
                    LogHelper.i(TAG, "====launch_recoding_view===")
                }
                else -> {

                }
            }
        }


        return mapOf()
    }
}