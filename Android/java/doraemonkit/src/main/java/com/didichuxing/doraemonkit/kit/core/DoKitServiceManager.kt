package com.didichuxing.doraemonkit.kit.core

import android.app.Activity

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
    private var mDokitServices: List<DoKitServiceInterface>? = null

    fun register(dokitServices: List<DoKitServiceInterface>) {
        mDokitServices = dokitServices
    }

    fun dispatch(activityOverrideEnum: DoKitServiceEnum, activity: Activity) {
        if (mDokitServices == null) {
            return
        }
        mDokitServices?.forEach {
            when (activityOverrideEnum) {
                DoKitServiceEnum.onCreate -> it.onCreate(activity)
                DoKitServiceEnum.onStart -> it.onStart(activity)
                DoKitServiceEnum.onResume -> it.onResume(activity)
                DoKitServiceEnum.onPause -> it.onPause(activity)
                DoKitServiceEnum.onStop -> it.onStop(activity)
                DoKitServiceEnum.onDestroy -> it.onDestroy(activity)
                DoKitServiceEnum.finish -> it.finish(activity)
                DoKitServiceEnum.onConfigurationChanged -> it.onConfigurationChanged(
                    activity
                )
                DoKitServiceEnum.onBackPressed -> it.onBackPressed(activity)
                DoKitServiceEnum.dispatchTouchEvent -> it.dispatchTouchEvent(activity)
                DoKitServiceEnum.onBackground -> it.onBackground()
                DoKitServiceEnum.onForeground -> it.onForeground(activity::class.java.canonicalName!!)
                else -> it.other(activity)

            }
        }
    }
}