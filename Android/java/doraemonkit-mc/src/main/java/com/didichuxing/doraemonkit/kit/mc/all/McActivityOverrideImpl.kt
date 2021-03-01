package com.didichuxing.doraemonkit.kit.mc.all

import android.app.Activity
import com.didichuxing.doraemonkit.kit.core.DoKitActivityOverrideListener
import com.didichuxing.doraemonkit.kit.mc.server.DoKitWsServer
import com.didichuxing.doraemonkit.util.LogHelper

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020/12/9-17:40
 * 描    述：
 * 修订历史：
 * ================================================
 */
class McActivityOverrideImpl : DoKitActivityOverrideListener {
    companion object {
        const val TAG = "McActivityOverrideImpl"
    }

    override fun onCreate(activity: Activity) {
    }

    override fun onStart(activity: Activity) {
    }

    override fun onResume(activity: Activity) {
    }

    override fun onPause(activity: Activity) {
    }

    override fun onStop(activity: Activity) {
    }

    override fun onDestroy(activity: Activity) {
    }

    override fun finish(activity: Activity) {
//        if (McConstant.WS_MODE == WSMode.HOST) {
//            val wsEvent = WSEvent(
//                WSMode.HOST,
//                WSEType.ACTIVITY_FINISH,
//                mutableMapOf(
//                    "activityName" to activity::class.java.canonicalName!!,
//                    "command" to "finish"
//                ),
//                null
//            )
//            DoKitWsServer.send(wsEvent)
//        }
    }

    override fun onConfigurationChanged(activity: Activity) {

    }

    override fun onBackPressed(activity: Activity) {
        if (McConstant.WS_MODE == WSMode.HOST) {
            val wsEvent = WSEvent(
                WSMode.HOST,
                WSEType.ACTIVITY_BACK_PRESSED,
                mutableMapOf(
                    "activityName" to activity::class.java.canonicalName!!,
                    "command" to "onBackPressed"
                ),
                null
            )
            DoKitWsServer.send(wsEvent)
        }
    }

    override fun dispatchTouchEvent(activity: Activity) {

    }

    override fun other(activity: Activity) {
    }

    override fun onForeground(className: String) {
        if (McConstant.WS_MODE == WSMode.HOST) {
            val wsEvent = WSEvent(
                WSMode.HOST,
                WSEType.APP_ON_FOREGROUND,
                mutableMapOf(
                    "command" to "onForeground",
                    "activityName" to className
                ),
                null
            )
            DoKitWsServer.send(wsEvent)
        }
    }

    override fun onBackground() {
        if (McConstant.WS_MODE == WSMode.HOST) {
            val wsEvent = WSEvent(
                WSMode.HOST,
                WSEType.APP_ON_BACKGROUND,
                mutableMapOf(
                    "command" to "onBackground"
                ),
                null
            )
            DoKitWsServer.send(wsEvent)
        }
    }


}