package com.didichuxing.doraemonkit.kit.core

import android.app.Activity
import android.os.Bundle

/**
 * Created by jintai on 2019/9/16.
 * dokitView intent
 */
data class DokitIntent(
    var targetClass: Class<out AbsDokitView>,
    var activity: Activity = Activity(),
    var bundle: Bundle? = null,
    var tag: String = "",
    var mode: DoKitViewLaunchMode = DoKitViewLaunchMode.SINGLE_INSTANCE
)


enum class DoKitViewLaunchMode {
    SINGLE_INSTANCE,
    /**
     * 倒计时
     */
    COUNTDOWN
}