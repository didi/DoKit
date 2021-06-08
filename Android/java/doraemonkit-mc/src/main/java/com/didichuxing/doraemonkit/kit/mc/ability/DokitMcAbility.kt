package com.didichuxing.doraemonkit.kit.mc.ability

import com.didichuxing.doraemonkit.kit.core.DokitAbility
import com.google.auto.service.AutoService

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2021/6/7-19:54
 * 描    述：
 * 修订历史：
 * ================================================
 */
@AutoService(DokitAbility::class)
class DokitMcAbility : DokitAbility {
    override fun moduleName(): String {
        return "DoKit_MC"
    }

    override fun getModuleFunctions(): Map<String, Any> {
        return mapOf("okhttp_interceptor" to DokitMcInterceptor())
    }
}