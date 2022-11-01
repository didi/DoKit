package com.didichuxing.doraemonkit.kit.core

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020/12/9-17:00
 * 描    述：
 * 修订历史：
 * ================================================
 */
enum class DoKitServiceEnum {
    /**
     * 页面生命周期
     */
    onCreate,
    onStart,
    onResume,
    onPause,
    onStop,
    onDestroy,
    finish,

    /**
     * 页面事件
     */
    onConfigurationChanged,
    onBackPressed,
    dispatchTouchEvent,
    other,

    /**
     * app 切换到前台
     */
    onForeground,

    /**
     * app 切换到后台
     */
    onBackground

}
