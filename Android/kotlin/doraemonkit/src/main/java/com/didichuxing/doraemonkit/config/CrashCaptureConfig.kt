package com.didichuxing.doraemonkit.config

import com.didichuxing.doraemonkit.constant.SharedPrefsKey
import com.didichuxing.doraemonkit.util.SharedPrefsUtil

/**
 * Created by wangxueying on 2020-06-30.
 */
object CrashCaptureConfig {
    var isCrashCaptureOpen: Boolean
        get() = SharedPrefsUtil.getBoolean(SharedPrefsKey.CRASH_CAPTURE_OPEN, false)
        set(open) = SharedPrefsUtil.putBoolean(SharedPrefsKey.CRASH_CAPTURE_OPEN, open)
}
