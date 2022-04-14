package com.didichuxing.doraemonkit.kit.test.hook

import android.view.View
import de.robv.android.xposed.XC_MethodHook

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020/11/30-19:55
 * 描    述：hook view#OnTouchListener#onTouch
 * ================================================
 */
class ViewOnClickListenerEventHook : XC_MethodHook() {

    companion object {
        const val TAG = "ViewOnClickListenerEventHook"
    }

    override fun beforeHookedMethod(param: MethodHookParam?) {
        super.beforeHookedMethod(param)
        param?.let {
            val listener = it.args[0]
            if (listener != null && listener is View.OnClickListener) {
                it.args[0] = ViewOnClickListenerProxy(listener)
            }
        }
    }

}
