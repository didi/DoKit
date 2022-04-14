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

    fun dispatch(activityOverrideEnum: DokitServiceEnum, activity: Activity) {
        if (lifecycle == null) {
            val life = DoKitManager.getModuleProcessor(DoKitModule.MODULE_MC)?.values()
                ?.get("lifecycle")
            if (life != null) {
                lifecycle = life as DoKitLifecycleInterface
            }
        }

        when (activityOverrideEnum) {
            DokitServiceEnum.onCreate -> lifecycle?.onCreate(activity)
            DokitServiceEnum.onStart -> lifecycle?.onStart(activity)
            DokitServiceEnum.onResume -> lifecycle?.onResume(activity)
            DokitServiceEnum.onPause -> lifecycle?.onPause(activity)
            DokitServiceEnum.onStop -> lifecycle?.onStop(activity)
            DokitServiceEnum.onDestroy -> lifecycle?.onDestroy(activity)
            DokitServiceEnum.finish -> lifecycle?.finish(activity)
            DokitServiceEnum.onConfigurationChanged -> lifecycle?.onConfigurationChanged(
                activity
            )
            DokitServiceEnum.onBackPressed -> lifecycle?.onBackPressed(activity)
            DokitServiceEnum.dispatchTouchEvent -> lifecycle?.dispatchTouchEvent(activity)
            DokitServiceEnum.onBackground -> lifecycle?.onBackground(activity)
            DokitServiceEnum.onForeground -> lifecycle?.onForeground(activity)
            else -> lifecycle?.other(activity)

        }

    }
}
