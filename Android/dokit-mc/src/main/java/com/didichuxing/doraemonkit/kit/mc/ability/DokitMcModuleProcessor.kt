package com.didichuxing.doraemonkit.kit.mc.ability

import android.view.View
import com.didichuxing.doraemonkit.DoKit
import com.didichuxing.doraemonkit.constant.WSMode
import com.didichuxing.doraemonkit.kit.core.DoKitManager
import com.didichuxing.doraemonkit.kit.core.DokitAbility
import com.didichuxing.doraemonkit.kit.mc.all.DoKitMcManager
import com.didichuxing.doraemonkit.kit.mc.all.hook.View_onClickListenerEventHook
import com.didichuxing.doraemonkit.kit.mc.client.ClientDokitView
import com.didichuxing.doraemonkit.kit.mc.server.HostDokitView
import com.didichuxing.doraemonkit.kit.mc.server.RecordingDokitView
import com.didichuxing.doraemonkit.util.SPUtils
import de.robv.android.xposed.DexposedBridge

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

    override fun proceed(actions: Map<String, Any?>?): Map<String, Any> {
        actions?.let {
            when (actions["action"]) {
                "launch_host_view" -> {
                    DoKit.launchFloating(HostDokitView::class)

                }
                "launch_client_view" -> {
                    DoKit.launchFloating(ClientDokitView::class)
                }
                "launch_recoding_view" -> {
                    if (DoKitMcManager.IS_MC_RECODING ||
                        SPUtils.getInstance().getBoolean(DoKitMcManager.MC_CASE_RECODING_KEY, false)
                    ) {
                        DoKit.launchFloating(RecordingDokitView::class)
                        DoKitMcManager.IS_MC_RECODING = true
                        DoKitMcManager.MC_CASE_ID =
                            SPUtils.getInstance().getString(DoKitMcManager.MC_CASE_ID_KEY)
                        DoKitManager.WS_MODE = WSMode.RECORDING
                    } else {
                    }
                }
                "mc_custom_event" -> {
                    DoKitMcManager.sendCustomEvent(
                        actions["eventType"] as String,
                        actions["view"] as View?,
                        actions["param"] as Map<String, String>?
                    )
                }
                "global_hook" -> {
                    DexposedBridge.findAndHookMethod(
                        View::class.java, "setOnClickListener",
                        View.OnClickListener::class.java, View_onClickListenerEventHook()
                    )

                }

                else -> {

                }
            }
        }
        return mapOf()
    }
}