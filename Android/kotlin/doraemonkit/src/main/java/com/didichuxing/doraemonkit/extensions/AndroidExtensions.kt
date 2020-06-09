package com.didichuxing.doraemonkit.extensions

import android.os.Build

/**
 * @author lostjobs created on 2020/6/9
 */

inline fun Any.runGreaterThanAndroidX(targetVersion: Int, runnable: () -> Unit) {
  if (Build.VERSION.SDK_INT >= targetVersion) {
    runnable()
  }
}

inline fun Any.runBelowAndroidX(targetVersion: Int, runnable: () -> Unit) {
  if (Build.VERSION.SDK_INT < targetVersion) {
    runnable()
  }
}

inline fun Any.runGreaterThanAndroidP(runnable: () -> Unit) =
  runGreaterThanAndroidX(Build.VERSION_CODES.P, runnable)

inline fun Any.runBelowAndroidP(runnable: () -> Unit) =
  runBelowAndroidX(Build.VERSION_CODES.P, runnable)

inline fun Any.runGreaterThanAndroidN(runnable: () -> Unit) =
  runGreaterThanAndroidX(Build.VERSION_CODES.N, runnable)

inline fun Any.runBelowAndroidN(runnable: () -> Unit) =
  runBelowAndroidX(Build.VERSION_CODES.N, runnable)

inline fun Any.runGreaterThanAndroidM(runnable: () -> Unit) =
  runGreaterThanAndroidX(Build.VERSION_CODES.M, runnable)

inline fun Any.runBelowAndroidM(runnable: () -> Unit) =
  runBelowAndroidX(Build.VERSION_CODES.M, runnable)