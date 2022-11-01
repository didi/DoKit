package com.didichuxing.doraemonkit.kit.core

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2019-12-31-11:37
 * 描    述：
 * 修订历史：
 * ================================================
 */

data class ActivityLifecycleStatusInfo(
    var isInvokeStopMethod: Boolean? = false,
    var lifeCycleStatus: DoKitLifeCycleStatus? = DoKitLifeCycleStatus.CREATED,
    var activityName: String? = ""
)


enum class DoKitLifeCycleStatus {
    /**
     * Activity 创建
     */
    CREATED,

    /**
     * Activity resume
     */
    RESUME,

    /**
     * Activity stop
     */
    STOPPED,

    /**
     * Activity destroy
     */
    DESTROYED
}