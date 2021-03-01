package com.didichuxing.doraemonkit.kit.mc.all.hook

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
class AccessibilityGetInstanceMethodHook : XC_MethodHook() {
    companion object {
        const val TAG = "AccessibilityGetInstanceMethodHook"
    }

    override fun afterHookedMethod(param: MethodHookParam?) {
        param?.let {
            if (it.result != null && it.result is Boolean) {
                if (!(it.result as Boolean)) {
                    it.result = true
                }
            }
        }
        super.afterHookedMethod(param)

    }


}