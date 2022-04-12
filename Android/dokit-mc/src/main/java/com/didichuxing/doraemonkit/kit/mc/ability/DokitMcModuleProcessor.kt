package com.didichuxing.doraemonkit.kit.mc.ability

import android.view.View
import com.didichuxing.doraemonkit.DoKit
import com.didichuxing.doraemonkit.kit.core.DokitAbility
import com.didichuxing.doraemonkit.kit.test.event.monitor.LifecycleMonitor
import com.didichuxing.doraemonkit.kit.mc.DoKitMcManager
import com.didichuxing.doraemonkit.kit.mc.net.DokitMcConnectManager
import com.didichuxing.doraemonkit.kit.mc.ui.client.ClientDokitView
import com.didichuxing.doraemonkit.kit.test.mock.http.DokitMcInterceptor
import com.didichuxing.doraemonkit.kit.mc.ui.host.HostDokitView
import com.didichuxing.doraemonkit.kit.mc.ui.record.RecordingDokitView
import com.didichuxing.doraemonkit.kit.test.mock.http.DokitMcProxyInterceptor
import com.didichuxing.doraemonkit.kit.test.util.McXposedHookUtils
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
            "okhttp_proxy_interceptor" to DokitMcProxyInterceptor(),
            "lifecycle" to LifecycleMonitor()
        )
    }

    override fun proceed(actions: Map<String, Any?>?): Map<String, Any> {
        try {
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
                            SPUtils.getInstance()
                                .getBoolean(DoKitMcManager.MC_CASE_RECODING_KEY, false)
                        ) {
                            DoKit.launchFloating(RecordingDokitView::class)
                            DoKitMcManager.IS_MC_RECODING = true
                            DoKitMcManager.MC_CASE_ID =
                                SPUtils.getInstance().getString(DoKitMcManager.MC_CASE_ID_KEY)
                        } else {

                        }
                    }
                    "mc_mode" -> {
                        return mapOf(Pair("mode", ""))
                    }
                    "mc_custom_event" -> {
                        DoKitMcManager.sendCustomEvent(
                            actions["eventType"] as String,
                            actions["view"] as View?,
                            actions["param"] as Map<String, String>?
                        )
                    }
                    "global_hook" -> {
                        McXposedHookUtils.globalHook()
                    }
                    "dokit_mc_connect_url" -> {
                        val map = mutableMapOf<String, String>()
                        val history = DokitMcConnectManager.currentConnectHistory
                        map["url"] = history?.url ?: ""
                    }

                    else -> {
                        LogHelper.e("DokitMcModuleProcessor", "not action ${actions["action"]}")
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return mapOf()
    }
}
