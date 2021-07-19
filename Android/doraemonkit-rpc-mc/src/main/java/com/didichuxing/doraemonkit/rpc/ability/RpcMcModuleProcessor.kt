package com.didichuxing.doraemonkit.rpc.ability

import com.didichuxing.doraemonkit.kit.core.DokitAbility

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2021/6/7-19:50
 * 描    述：
 * 修订历史：
 * ================================================
 */
class RpcMcModuleProcessor : DokitAbility.DokitModuleProcessor {

    override fun values(): Map<String, Any> {
        return mapOf(
            "rpc_interceptor" to RpcMcInterceptor()
        )
    }

    override fun proceed(actions: Map<String, Any?>?): Map<String, Any> {
        return mapOf()
    }
}