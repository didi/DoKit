package com.didichuxing.doraemonkit.gps_mock.sysservicehook

import android.content.Context
import android.os.Build
import android.os.IBinder
import android.os.IInterface
import com.didichuxing.doraemonkit.gps_mock.gpsmock.BaseServiceHooker
import com.didichuxing.doraemonkit.gps_mock.gpsmock.MethodHandler
import com.didichuxing.doraemonkit.util.ReflectUtils

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2021/6/10-14:24
 * 描    述：ActivityTaskMangerHooker  hook android 10.0新增
 * 修订历史：
 * ================================================
 */
class ActivityTaskMangerHooker : BaseServiceHooker() {

    /**
     * Context.ACTIVITY_TASK_SERVICE 属于隐藏API
     */
    override fun serviceName(): String {
        return "activity_task"
    }

    override fun stubName(): String {
        return "android.app.IActivityTaskManager\$Stub"
    }

    override fun registerMethodHandlers(): Map<String, MethodHandler> {
        return mapOf("startActivity" to StartActivityMethodHandler())
    }

    /**
     * https://www.androidos.net.cn/android/7.0.0_r31/xref/frameworks/base/core/java/android/app/IActivityManager.java
     */
    override fun replaceBinderProxy(
        context: Context,
        proxy: IBinder
    ) {
        //val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE)
        val customService =
            ReflectUtils.reflect(stubName()).method("asInterface", proxy).get<IInterface>()

        when (Build.VERSION.SDK_INT) {
            //29 android 10.0
            Build.VERSION_CODES.Q,
            30 -> {
                val iActivityManagerSingleton =
                    ReflectUtils.reflect("android.app.ActivityTaskManager")
                        .field("IActivityTaskManagerSingleton")
                        .get<Any>()
                ReflectUtils.reflect(iActivityManagerSingleton).field("mInstance", customService)
            }
        }

    }



}
