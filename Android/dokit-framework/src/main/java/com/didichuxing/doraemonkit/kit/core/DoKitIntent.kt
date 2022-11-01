package com.didichuxing.doraemonkit.kit.core

import android.app.Activity
import android.os.Bundle
import com.didichuxing.doraemonkit.extension.tagName
import com.didichuxing.doraemonkit.util.ActivityUtils
import kotlin.reflect.KClass

/**
 * Created by jintai on 2019/9/16.
 * dokitView intent
 */
data class DoKitIntent(
    var targetClass: Class<out AbsDoKitView>,
    var activity: Activity = ActivityUtils.getTopActivity(),
    var bundle: Bundle? = null,
    var tag: String = targetClass.tagName,
    var mode: DoKitViewLaunchMode = DoKitViewLaunchMode.SINGLE_INSTANCE
)


enum class DoKitViewLaunchMode {
    SINGLE_INSTANCE,
    /**
     * 倒计时
     */
    COUNTDOWN
}
