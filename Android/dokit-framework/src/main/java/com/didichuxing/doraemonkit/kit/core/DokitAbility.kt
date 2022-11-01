package com.didichuxing.doraemonkit.kit.core

import com.didichuxing.doraemonkit.constant.DoKitModule

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2021/6/7-19:42
 * 描    述：具体的某块具有哪些能力
 * 修订历史：
 * ================================================
 */
interface DokitAbility {


    fun init()

    /**
     * 模块名
     */
    fun moduleName(): DoKitModule

    /**
     *注册模块能力处理器
     */
    fun getModuleProcessor(): DokitModuleProcessor


    interface DokitModuleProcessor {
        val TAG: String
            get() = this.javaClass.simpleName

        fun values(): Map<String, Any?>

        fun proceed(actions: Map<String, Any?>? = null): Map<String, Any>
    }
}
