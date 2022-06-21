package com.didichuxing.doraemonkit.gps_mock.sysservicehook

import android.app.ActivityManager
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
 * 描    述：ActivityManagerService hook
 * 修订历史：
 * ================================================
 */
class ActivityMangerHooker : BaseServiceHooker() {
    companion object {
        const val TAG = "ActivityMangerHooker"
    }

    override fun serviceName(): String {
        return Context.ACTIVITY_SERVICE
    }

    override fun stubName(): String {
        return "android.app.IActivityManager\$Stub"
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
            //16
            Build.VERSION_CODES.JELLY_BEAN,
                //17
            Build.VERSION_CODES.JELLY_BEAN_MR1,
                //18
            Build.VERSION_CODES.JELLY_BEAN_MR2,
                //19
            Build.VERSION_CODES.KITKAT,
                //20
            Build.VERSION_CODES.KITKAT_WATCH,
                //21
            Build.VERSION_CODES.LOLLIPOP,
                //22
            Build.VERSION_CODES.LOLLIPOP_MR1,
                //23
            Build.VERSION_CODES.M,
                //24
            Build.VERSION_CODES.N,
                //25
            Build.VERSION_CODES.N_MR1
            -> {
                val gDefault =
                    ReflectUtils.reflect("android.app.ActivityManagerNative").field("gDefault")
                        .get<Any>()
                //替换ActivityManagerNative中gDefault中mInstance的值为proxy
                ReflectUtils.reflect(gDefault).field("mInstance", customService)
            }
            //26
            Build.VERSION_CODES.O,
                //27
            Build.VERSION_CODES.O_MR1,
                //28
            Build.VERSION_CODES.P,
                //29
            Build.VERSION_CODES.Q,
            30 -> {
                val iActivityManagerSingleton =
                    ReflectUtils.reflect(ActivityManager::class.java)
                        .field("IActivityManagerSingleton")
                        .get<Any>()
                ReflectUtils.reflect(iActivityManagerSingleton).field("mInstance", customService)
            }
        }

    }

}
