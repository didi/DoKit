package com.didichuxing.doraemonkit.kit.mc.all.hook

import android.view.accessibility.AccessibilityManager
import de.robv.android.xposed.XC_MethodHook

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020/11/30-19:55
 * 描    述：View# dispatchTouchEvent hook
 * 修订历史：
 * ================================================
 */
class AccessibilityManagerConstructorHook : XC_MethodHook() {

    companion object {
        const val TAG = "AccessibilityManagerConstructorHook"
        var managerProxy: AccessibilityManagerProxy? = null
    }

    override fun afterHookedMethod(param: MethodHookParam?) {
        if (param != null) {
            val manager = param.thisObject as AccessibilityManager
            managerProxy = AccessibilityManagerProxy(manager)
//            param.thisObject = managerProxy
//            param.result = managerProxy
        }
        super.afterHookedMethod(param)
    }

    override fun beforeHookedMethod(param: MethodHookParam?) {
        super.beforeHookedMethod(param)
    }

    override fun call(param: Param?) {
        super.call(param)
    }
}
