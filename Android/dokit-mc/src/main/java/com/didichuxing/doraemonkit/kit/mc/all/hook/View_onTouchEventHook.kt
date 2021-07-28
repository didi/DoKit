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
class View_onTouchEventHook : XC_MethodHook() {

    companion object {
        const val TAG = "onTouchEventHook"
    }


    /**
     * https://developer.android.google.cn/reference/android/view/accessibility/AccessibilityEvent
     */
    override fun afterHookedMethod(param: MethodHookParam?) {
        super.afterHookedMethod(param)
        if (DoKitManager.WS_MODE != WSMode.HOST) {
            return
        }
        param?.let {
            val view = it.thisObject as View
            val motionEvent = it.args[0] as MotionEvent
            val result: Boolean = it.result as Boolean
            if(result){
                LogHelper.i(TAG, "view===>$view   motionEvent===>$motionEvent   result===>$result")
            }
        }

    }


}