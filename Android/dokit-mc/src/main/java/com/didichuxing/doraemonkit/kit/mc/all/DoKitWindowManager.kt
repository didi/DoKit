package com.didichuxing.doraemonkit.kit.mc.all

import android.app.Activity
import android.os.Build
import android.view.View
import android.view.ViewParent
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityManager
import com.didichuxing.doraemonkit.kit.mc.all.hook.AccessibilityGetInstanceMethodHook
import com.didichuxing.doraemonkit.kit.mc.all.hook.View_onInitializeAccessibilityEventHook
import com.didichuxing.doraemonkit.kit.mc.util.McHookUtil
import de.robv.android.xposed.DexposedBridge
import de.robv.android.xposed.XposedHelpers

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020/12/14-20:06
 * 描    述：
 * 修订历史：
 * ================================================
 */
object DoKitWindowManager {

    var ROOT_VIEWS: List<ViewParent>? = null

    var HOOK_ENABLE: Boolean = false

    fun hookWindowManagerGlobal() {
        ROOT_VIEWS = McHookUtil.getRootViewsFromWmg()

    }


    /**
     * hook
     */
    fun runTimeHook(activity: Activity?) {
        HOOK_ENABLE = true
        try {
            //对于Android9.0以下的系统 需要反射将AccessibilityManager.mIsEnabled变量改成true
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
                McHookUtil.hookAccessibilityManager(activity)
            }

            //绕过无障碍的权限
            DexposedBridge.findAndHookMethod(
                AccessibilityManager::class.java,
                "isEnabled",
                AccessibilityGetInstanceMethodHook()
            )

            //hook onInitializeAccessibilityEvent
//            val onInitializeAccessibilityEventMethod = XposedHelpers.findMethodExact(
//                View::class.java,
//                "onInitializeAccessibilityEvent",
//                AccessibilityEvent::class.java
//            )
//            DexposedBridge.hookMethod(
//                onInitializeAccessibilityEventMethod,
//                View_onInitializeAccessibilityEventHook()
//            )

            DexposedBridge.findAndHookMethod(
                View::class.java,
                "onInitializeAccessibilityEvent",
                AccessibilityEvent::class.java,
                View_onInitializeAccessibilityEventHook()
            )

            //hook View#onTouchEvent
//            DexposedBridge.findAndHookMethod(
//                View::class.java,
//                "onTouchEvent",
//                MotionEvent::class.java,
//                View_onTouchEventHook()
//            )

        } catch (e: Exception) {
            e.printStackTrace()
            HOOK_ENABLE = false
        }

    }
}
