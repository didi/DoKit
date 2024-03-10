package com.didichuxing.doraemonkit.kit.core

import android.app.Activity
import com.didichuxing.doraemonkit.constant.DoKitModule

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020/12/9-17:00
 * 描    述：
 * 修订历史：
 * ================================================
 */
object DoKitServiceManager {
    var lifecycle: DoKitLifecycleInterface? = null

    fun dispatch(activityOverrideEnum: DoKitServiceEnum, activity: Activity) {
        if (lifecycle == null) {
            val life = DoKitManager.getModuleProcessor(DoKitModule.MODULE_MC)?.values()
                ?.get("lifecycle")
            if (life != null) {
                lifecycle = life as DoKitLifecycleInterface
            }
        }

        when (activityOverrideEnum) {
            DoKitServiceEnum.onCreate -> lifecycle?.onCreate(activity)
            DoKitServiceEnum.onStart -> lifecycle?.onStart(activity)
            DoKitServiceEnum.onResume -> lifecycle?.onResume(activity)
            DoKitServiceEnum.onPause -> lifecycle?.onPause(activity)
            DoKitServiceEnum.onStop -> lifecycle?.onStop(activity)
            DoKitServiceEnum.onDestroy -> lifecycle?.onDestroy(activity)
            DoKitServiceEnum.finish -> lifecycle?.finish(activity)
            DoKitServiceEnum.onConfigurationChanged -> lifecycle?.onConfigurationChanged(
                activity
            )
            DoKitServiceEnum.onBackPressed -> lifecycle?.onBackPressed(activity)
            DoKitServiceEnum.dispatchTouchEvent -> lifecycle?.dispatchTouchEvent(activity)
            DoKitServiceEnum.onBackground -> lifecycle?.onBackground(activity)
            DoKitServiceEnum.onForeground -> lifecycle?.onForeground(activity)
            else -> lifecycle?.other(activity)

        }

    }
}
