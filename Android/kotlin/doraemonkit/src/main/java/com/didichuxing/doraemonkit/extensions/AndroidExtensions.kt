package com.didichuxing.doraemonkit.extensions

import android.os.Build

/**
 * @author lostjobs created on 2020/6/9
 */

typealias Task = () -> Unit

inline fun Any.runStartWithAndroidX(targetVersion: Int, below: Task = {}, above: Task) {
    if (Build.VERSION.SDK_INT >= targetVersion) {
        above()
    } else {
        below()
    }
}


inline fun Any.runStartWithAndroidP(below: Task = {}, above: Task) =
        runStartWithAndroidX(Build.VERSION_CODES.P, below, above)


inline fun Any.runStartWithAndroidN(below: Task = {}, above: Task) =
        runStartWithAndroidX(Build.VERSION_CODES.N, below, above)


inline fun Any.runStartWithAndroidM(below: Task = {}, above: Task) =
        runStartWithAndroidX(Build.VERSION_CODES.M, below, above)

