package com.didichuxing.doraemonkit.kit.mc.all.hook

import com.didichuxing.doraemonkit.util.LogHelper
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
class AccessibilityManagerMethodHook : XC_MethodHook() {

    companion object {
        const val TAG = "AccessibilityManagerMethodHook"
    }

    override fun afterHookedMethod(param: MethodHookParam?) {
        LogHelper.e(TAG, "afterHookedMethod()")
        if (param != null && AccessibilityManagerConstructorHook.managerProxy != null) {
//            param.result = AccessibilityManagerConstructorHook.managerProxy
        }
        super.afterHookedMethod(param)
    }

    override fun beforeHookedMethod(param: MethodHookParam?) {
        super.beforeHookedMethod(param)
    }

}
