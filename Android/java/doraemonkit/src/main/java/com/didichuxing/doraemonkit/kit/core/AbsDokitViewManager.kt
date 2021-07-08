package com.didichuxing.doraemonkit.kit.core

import android.app.Activity
import com.didichuxing.doraemonkit.constant.DoKitConstant
import com.didichuxing.doraemonkit.constant.DoKitConstant.WS_MODE
import com.didichuxing.doraemonkit.constant.WSMode
import com.didichuxing.doraemonkit.kit.health.CountDownDokitView

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2019-09-28-15:18
 * 描    述：页面浮标管理类接口
 * 修订历史：
 * ================================================
 */
abstract class AbsDokitViewManager : DokitViewManagerInterface {
    protected var TAG = this.javaClass.simpleName

    /**
     * 添加倒计时DokitView
     */
    fun attachCountDownDokitView(activity: Activity) {
        if (!DoKitConstant.APP_HEALTH_RUNNING) {
            return
        }
        if (activity is UniversalActivity) {
            return
        }
        val dokitIntent = DokitIntent(CountDownDokitView::class.java)
        dokitIntent.mode = DokitIntent.MODE_ONCE
        attach(dokitIntent)
    }

    /**
     * 添加一机多控标识
     *
     * @param activity
     */
    fun attachMcDokitView(activity: Activity) {
        if (WS_MODE === WSMode.UNKNOW) {
            return
        }
    }
}