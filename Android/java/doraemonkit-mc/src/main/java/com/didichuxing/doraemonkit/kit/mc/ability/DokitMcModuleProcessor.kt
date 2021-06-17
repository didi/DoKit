package com.didichuxing.doraemonkit.kit.mc.ability

import com.didichuxing.doraemonkit.kit.core.DokitAbility
import com.didichuxing.doraemonkit.kit.core.DokitIntent
import com.didichuxing.doraemonkit.kit.core.DokitViewManager
import com.didichuxing.doraemonkit.kit.health.CountDownDokitView
import com.didichuxing.doraemonkit.kit.mc.client.ClientDokitView
import com.didichuxing.doraemonkit.kit.mc.server.HostDokitView
import com.google.auto.service.AutoService

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
        return mapOf("okhttp_interceptor" to DokitMcInterceptor())
    }

    override fun proceed(actions: Map<String, Any>?): Map<String, Any> {
        actions?.let {
            when (actions["action"]) {
                "launch_host_view" -> {
                    val dokitIntent = DokitIntent(HostDokitView::class.java)
                    dokitIntent.mode = DokitIntent.MODE_ONCE
                    DokitViewManager.getInstance().attach(dokitIntent)
                }
                "launch_client_view" -> {
                    val dokitIntent = DokitIntent(ClientDokitView::class.java)
                    dokitIntent.mode = DokitIntent.MODE_ONCE
                    DokitViewManager.getInstance().attach(dokitIntent)
                }
                else -> {

                }
            }
        }


        return mapOf()
    }
}