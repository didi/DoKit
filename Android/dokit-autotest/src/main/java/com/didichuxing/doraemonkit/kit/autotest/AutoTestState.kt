package com.didichuxing.doraemonkit.kit.autotest

import android.app.Activity
import android.view.View
import com.didichuxing.doraemonkit.kit.test.event.ControlEvent

data class AutoTestState(
    val activity: Activity?,
    val view: View?,
    val controlEvent: ControlEvent,
    val message: AutoTestMessage,
    val success: Boolean = true
)
