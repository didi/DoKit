package com.didichuxing.doraemonkit

import android.app.Application
import android.util.Log
import com.blankj.utilcode.util.ActivityUtils
import com.didichuxing.doraemonkit.abridge.AbridgeCallBack
import com.didichuxing.doraemonkit.abridge.IBridge
import com.didichuxing.doraemonkit.constant.DokitConstant
import com.didichuxing.doraemonkit.kit.health.AppHealthInfoUtil
import com.didichuxing.doraemonkit.kit.health.model.AppHealthInfo.DataBean.LeakBean
import com.squareup.leakcanary.LeakCanary

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2019-10-17-10:11
 * 描    述：
 * 修订历史：
 * ================================================
 */
internal object LeakCanaryManager {
    private const val TAG = "LeakCanaryManager"
    fun install(app: Application?) {
        if (LeakCanary.isInAnalyzerProcess(app!!)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return
        }
        LeakCanary.install(app)
    }

    /**
     * 初始化跨进程框架
     * 接受leakcanary 进程泄漏传递过来的数据
     */
    fun initAidlBridge(application: Application) {
        if (!DokitConstant.APP_HEALTH_RUNNING) {
            return
        }
        IBridge.init(application, application.packageName, IBridge.AbridgeType.AIDL)
        IBridge.registerAIDLCallBack(object : AbridgeCallBack {
            override fun receiveMessage(message: String?) {
                try {
                    Log.i(TAG, "====aidl=====>$message")
                    if (DokitConstant.APP_HEALTH_RUNNING) {
                        val leakBean = LeakBean()
                        leakBean.page = ActivityUtils.getTopActivity().javaClass.canonicalName
                        leakBean.detail = message
                        AppHealthInfoUtil.instance.addLeakInfo(leakBean)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        })
    }
}