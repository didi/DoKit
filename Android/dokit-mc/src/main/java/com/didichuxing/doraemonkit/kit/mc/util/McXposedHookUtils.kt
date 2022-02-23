package com.didichuxing.doraemonkit.kit.mc.util

import android.app.Activity
import android.os.Build
import android.view.View
import android.view.ViewParent
import android.view.accessibility.AccessibilityEvent
import com.didichuxing.doraemonkit.kit.mc.all.hook.ViewOnClickListenerEventHook
import com.didichuxing.doraemonkit.kit.mc.all.hook.ViewOnInitializeAccessibilityEventHook
import de.robv.android.xposed.DexposedBridge

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020/12/14-20:06
 * 描    述：
 * 修订历史：
 * ================================================
 * hook 实现
 */
object McXposedHookUtils {

    var ROOT_VIEWS: List<ViewParent>? = null

    var HOOK_ENABLE: Boolean = false

    fun hookWindowManagerGlobal() {
        ROOT_VIEWS = McReflectHookUtils.getRootViewsFromWindowManageGlobal()
    }


    /**
     * hook
     */
    fun runTimeHook(activity: Activity?) {
        HOOK_ENABLE = true
        try {
            //对于Android 10.0 以下的系统,需要反射将AccessibilityManager.mIsEnabled变量改成true
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
                McReflectHookUtils.hookAccessibilityManager(activity)
            }

            //绕过无障碍的权限
//            DexposedBridge.findAndHookMethod(
//                AccessibilityManager::class.java,
//                "isEnabled",
//                AccessibilityGetInstanceMethodHook()
//            )

            DexposedBridge.findAndHookMethod(
                View::class.java,
                "onInitializeAccessibilityEvent",
                AccessibilityEvent::class.java,
                ViewOnInitializeAccessibilityEventHook()
            )

        } catch (e: Exception) {
            e.printStackTrace()
            HOOK_ENABLE = false
        }

    }

    fun globalHook() {
        //hook onClick事件
        DexposedBridge.findAndHookMethod(
            View::class.java,
            "setOnClickListener",
            View.OnClickListener::class.java,
            ViewOnClickListenerEventHook()
        )
    }
}
