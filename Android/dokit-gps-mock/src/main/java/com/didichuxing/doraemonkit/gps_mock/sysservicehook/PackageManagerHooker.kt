package com.didichuxing.doraemonkit.gps_mock.sysservicehook

import android.content.Context
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
class PackageManagerHooker : BaseServiceHooker() {

    /**
     * Context.ACTIVITY_TASK_SERVICE 属于隐藏API
     */
    override fun serviceName(): String {
        return "package"
    }

    override fun stubName(): String {
        return "android.content.pm.IPackageManager\$Stub"
    }

    override fun registerMethodHandlers(): Map<String, MethodHandler> {
        return mapOf("getInstalledApplications" to GetInstalledApplicationsMethodHandler())
    }

    /**
     * https://www.androidos.net.cn/android/7.0.0_r31/xref/frameworks/base/core/java/android/app/IActivityManager.java
     */
    override fun replaceBinderProxy(
        context: Context,
        proxy: IBinder
    ) {
        val customService =
            ReflectUtils.reflect(stubName()).method("asInterface", proxy).get<IInterface>()

        val activityThread =
            ReflectUtils.reflect("android.app.ActivityThread").method("currentActivityThread")
                .get<Any>()
        //替换ActivityThread中的sPackageManager
        ReflectUtils.reflect(activityThread).field("sPackageManager", customService)
        //ApplicationPackageManager
        val packageManager = context.packageManager
        ReflectUtils.reflect(packageManager).field("mPM", customService)
    }


}
