/*
 * Activity ktx declaring
 *
 * @author alvince.zy@gmail.com
 */

package com.didichuxing.doraemonkit.weex.util

import android.app.Activity
import android.os.Build
import android.widget.FrameLayout

/**
 * Find the [Activity] content's container [FrameLayout] which has the id `R.id.content`
 */
fun Activity.containerView(): FrameLayout? {
    if (isFinishing) {
        return null
    }
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && isDestroyed) {
        return null
    }
    return window?.decorView?.findViewById(android.R.id.content)
}
