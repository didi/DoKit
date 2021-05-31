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
object DokitServiceManager {
    private var mDokitServices: List<DokitServiceInterface>? = null

    fun register(dokitServices: List<DokitServiceInterface>) {
        mDokitServices = dokitServices
    }

    fun dispatch(activityOverrideEnum: DokitServiceEnum, activity: Activity) {
        if (mDokitServices == null) {
            return
        }
        mDokitServices?.forEach {
            when (activityOverrideEnum) {
                DokitServiceEnum.onCreate -> it.onCreate(activity)
                DokitServiceEnum.onStart -> it.onStart(activity)
                DokitServiceEnum.onResume -> it.onResume(activity)
                DokitServiceEnum.onPause -> it.onPause(activity)
                DokitServiceEnum.onStop -> it.onStop(activity)
                DokitServiceEnum.onDestroy -> it.onDestroy(activity)
                DokitServiceEnum.finish -> it.finish(activity)
                DokitServiceEnum.onConfigurationChanged -> it.onConfigurationChanged(
                    activity
                )
                DokitServiceEnum.onBackPressed -> it.onBackPressed(activity)
                DokitServiceEnum.dispatchTouchEvent -> it.dispatchTouchEvent(activity)
                DokitServiceEnum.onBackground -> it.onBackground()
                DokitServiceEnum.onForeground -> it.onForeground(activity::class.java.canonicalName!!)
                else -> it.other(activity)

            }
        }
    }
}