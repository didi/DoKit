package com.didichuxing.doraemonkit.kit.mc.ability

import com.didichuxing.doraemonkit.constant.DoKitModule
import com.didichuxing.doraemonkit.kit.core.DokitAbility
import com.didichuxing.doraemonkit.util.LogHelper
import com.google.auto.service.AutoService

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2021/6/7-19:54
 * 描    述：
 * 修订历史：
 * ================================================
 * 模块能力相关代码
 */
@AutoService(DokitAbility::class)
class DoKitMcAbility : DokitAbility {

    private val TAG = "DoKitMcAbility"
    override fun init() {
        LogHelper.i(TAG, "init()")
    }

    override fun moduleName(): DoKitModule {
        return DoKitModule.MODULE_MC
    }

    override fun getModuleProcessor(): DokitAbility.DokitModuleProcessor {
        return DoKitMcModuleProcessor()
    }

}
