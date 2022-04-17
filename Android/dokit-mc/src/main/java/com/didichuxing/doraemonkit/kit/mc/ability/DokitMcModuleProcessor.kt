package com.didichuxing.doraemonkit.kit.mc.ability

import android.view.View
import com.didichuxing.doraemonkit.DoKit
import com.didichuxing.doraemonkit.kit.core.DokitAbility
import com.didichuxing.doraemonkit.kit.test.event.monitor.LifecycleEventMonitor
import com.didichuxing.doraemonkit.kit.mc.DoKitMcManager
import com.didichuxing.doraemonkit.kit.mc.net.DokitMcConnectManager
import com.didichuxing.doraemonkit.kit.mc.oldui.client.ClientDokitView
import com.didichuxing.doraemonkit.kit.test.mock.http.DoKitMockInterceptor
import com.didichuxing.doraemonkit.kit.mc.oldui.host.HostDokitView
import com.didichuxing.doraemonkit.kit.mc.oldui.record.RecordingDokitView
import com.didichuxing.doraemonkit.kit.test.DoKitTestManager
import com.didichuxing.doraemonkit.kit.test.mock.http.DoKitProxyMockInterceptor
import com.didichuxing.doraemonkit.kit.test.utils.XposedHookUtil
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
            "okhttp_interceptor" to DoKitMockInterceptor(),
            "okhttp_proxy_interceptor" to DoKitProxyMockInterceptor(),
            "lifecycle" to LifecycleEventMonitor()
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
                        val mode = if (DoKitTestManager.isHostMode()) {
                            "host"
                        } else if (DoKitTestManager.isClientMode()) {
                            "client"
                        } else {
                            "unknown"
                        }
                        return mapOf(Pair("mode", mode))
                    }
                    "mc_custom_event" -> {
                        DoKitMcManager.sendCustomEvent(
                            actions["eventType"] as String,
                            actions["view"] as View?,
                            actions["param"] as Map<String, String>?
                        )
                    }
                    "global_hook" -> {
                        XposedHookUtil.globalHook()
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
