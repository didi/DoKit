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
object DoKitActivityOverrideManager {
    private val activityOverrideListener: MutableList<DoKitActivityOverrideListener> by lazy {
        mutableListOf<DoKitActivityOverrideListener>()
    }

    fun register(doKitActivityOverrideListeners: MutableList<DoKitActivityOverrideListener>) {
        activityOverrideListener.clear()
        activityOverrideListener.addAll(doKitActivityOverrideListeners)
    }

    fun dispatch(activityOverrideEnum: DoKitActivityOverrideEnum, activity: Activity) {
        activityOverrideListener.forEach {
            when (activityOverrideEnum) {
                DoKitActivityOverrideEnum.onCreate -> it.onCreate(activity)
                DoKitActivityOverrideEnum.onStart -> it.onStart(activity)
                DoKitActivityOverrideEnum.onResume -> it.onResume(activity)
                DoKitActivityOverrideEnum.onPause -> it.onPause(activity)
                DoKitActivityOverrideEnum.onStop -> it.onStop(activity)
                DoKitActivityOverrideEnum.onDestroy -> it.onDestroy(activity)
                DoKitActivityOverrideEnum.finish -> it.finish(activity)
                DoKitActivityOverrideEnum.onConfigurationChanged -> it.onConfigurationChanged(
                    activity
                )
                DoKitActivityOverrideEnum.onBackPressed -> it.onBackPressed(activity)
                DoKitActivityOverrideEnum.dispatchTouchEvent -> it.dispatchTouchEvent(activity)
                DoKitActivityOverrideEnum.onBackground -> it.onBackground()
                DoKitActivityOverrideEnum.onForeground -> it.onForeground(activity::class.java.canonicalName!!)
                else -> it.other(activity)

            }
        }
    }
}