package com.didichuxing.doraemonkit.kit.test.utils

import android.content.Context
import android.os.Build
import android.view.View
import android.view.ViewParent
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityManager
import com.didichuxing.doraemonkit.kit.test.hook.AccessibilityGetInstanceMethodHook
import com.didichuxing.doraemonkit.kit.test.hook.ViewOnClickListenerEventHook
import com.didichuxing.doraemonkit.kit.test.hook.ViewOnInitializeAccessibilityEventHook
import com.didichuxing.doraemonkit.util.Utils
import de.robv.android.xposed.DexposedBridge
import java.lang.reflect.Method

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
object XposedHookUtil {

    var ROOT_VIEWS: List<ViewParent>? = null

    private var runTimeHookEnable: Boolean = false


    private val accessibilityEventHook = ViewOnInitializeAccessibilityEventHook()

    /**
     * 运行时hook是否开启
     */
    fun isRunTimeHookEnable(): Boolean {
        return runTimeHookEnable
    }

    /**
     * 开始运行时 hook
     */
    fun startRunTimeHook(context: Context? = Utils.getApp()) {
        runTimeHookEnable = true
        ReflectHookUtil.hookAccessibilityManager(context, true)
        hookAccessibilityEvent()
    }

    /**
     * 停止运行时 hook
     */
    fun stopRunTimeHook(context: Context? = Utils.getApp()) {
        ReflectHookUtil.hookAccessibilityManager(context, false)
        unHookAccessibilityEvent()
        runTimeHookEnable = false
    }

    fun hookAccessibilityManager() {
        //绕过无障碍的权限
        DexposedBridge.findAndHookMethod(
            AccessibilityManager::class.java,
            "isEnabled",
            AccessibilityGetInstanceMethodHook()
        )
    }

    private fun hookAccessibilityEvent() {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
            try {
                DexposedBridge.findAndHookMethod(
                    View::class.java,
                    "onInitializeAccessibilityEvent",
                    AccessibilityEvent::class.java,
                    accessibilityEventHook
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun unHookAccessibilityEvent() {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
            try {
                val method: Method = View::class.java.getDeclaredMethod("onInitializeAccessibilityEvent", AccessibilityEvent::class.java)
                DexposedBridge.unhookMethod(method, accessibilityEventHook)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * 全局Hook应用启动后直接Hook全部实现
     */
    fun globalHook() {
        //hook onClick事件
        hookViewOnClickListener()
        //hook WindowGlobal
        hookWindowManagerGlobal()
    }

    private fun hookViewOnClickListener() {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
            DexposedBridge.findAndHookMethod(
                View::class.java,
                "setOnClickListener",
                View.OnClickListener::class.java,
                ViewOnClickListenerEventHook()
            )
        }
    }

    private fun hookWindowManagerGlobal() {
        ROOT_VIEWS = ReflectHookUtil.getRootViewsFromWindowManageGlobal()
    }
}
