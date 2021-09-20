package com.didichuxing.doraemonkit.kit.mc.all.hook

import android.view.MotionEvent
import android.view.View
import com.didichuxing.doraemonkit.kit.core.DoKitManager
import com.didichuxing.doraemonkit.constant.WSMode
import com.didichuxing.doraemonkit.util.LogHelper
import de.robv.android.xposed.XC_MethodHook

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020/11/30-19:55
 * 描    述：hook view#OnTouchListener#onTouch
 * ================================================
 */
class View_onClickListenerEventHook : XC_MethodHook() {

    companion object {
        const val TAG = "OnClickListenerEventHook"
    }

    override fun beforeHookedMethod(param: MethodHookParam?) {
        super.beforeHookedMethod(param)
        param?.let {
            val listener = it.args[0]
            if (listener != null && listener is View.OnClickListener) {
                it.args[0] = DoKitOnClickListener(listener)
            }
        }
    }


}