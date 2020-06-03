package com.didichuxing.doraemonkit.util

import android.app.Activity
import android.app.ActivityManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Build
import android.os.Process
import android.provider.Settings
import android.text.TextUtils
import com.didichuxing.doraemonkit.constant.DokitConstant
import com.didichuxing.doraemonkit.util.LogHelper.e
import java.util.regex.Pattern

/**
 * Created by wanglikun on 2018/12/21.
 */
object SystemUtil {
    private const val TAG = "SystemUtil"
    private val VERSION_NAME_PATTERN = Pattern.compile("(\\d+\\.\\d+\\.\\d+)-*.*")
    private var sAppVersion: String? = null
    private var sAppVersionCode = -1
    private var sPackageName: String? = null
    private var sAppName: String? = null
    fun getVersionName(context: Context): String? {
        return if (!TextUtils.isEmpty(sAppVersion)) {
            sAppVersion
        } else {
            var appVersion = ""
            try {
                val pkgName = context.applicationInfo.packageName
                appVersion = context.packageManager.getPackageInfo(pkgName, 0).versionName
                if (appVersion != null && appVersion.length > 0) {
                    val matcher = VERSION_NAME_PATTERN.matcher(appVersion)
                    if (matcher.matches()) {
                        appVersion = matcher.group(1)
                        sAppVersion = appVersion
                    }
                }
            } catch (t: Throwable) {
                e(TAG, t.toString())
            }
            appVersion
        }
    }

    fun getVersionCode(context: Context): Int {
        return if (sAppVersionCode != -1) {
            sAppVersionCode
        } else {
            val versionCode: Int
            try {
                val pkgName = context.applicationInfo.packageName
                versionCode = context.packageManager.getPackageInfo(pkgName, 0).versionCode
                if (versionCode != -1) {
                    sAppVersionCode = versionCode
                }
            } catch (t: Throwable) {
                e(TAG, t.toString())
            }
            sAppVersionCode
        }
    }

    fun getPackageName(context: Context): String? {
        if (TextUtils.isEmpty(sPackageName)) {
            sPackageName = context.packageName
        }
        return sPackageName
    }

    fun getAppName(context: Context): String? {
        return if (!TextUtils.isEmpty(sAppName)) {
            sAppName
        } else {
            var packageManager: PackageManager? = null
            var applicationInfo: ApplicationInfo? = null
            packageManager = context.packageManager
            try {
                applicationInfo = packageManager.getApplicationInfo(context.packageName, 0)
            } catch (e: PackageManager.NameNotFoundException) {
                e(TAG, e.toString())
            }
            sAppName = packageManager.getApplicationLabel(applicationInfo) as String
            sAppName
        }
    }

    fun obtainProcessName(context: Context): String? {
        val pid = Process.myPid()
        val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val listTaskInfo = am.runningAppProcesses
        if (listTaskInfo != null && !listTaskInfo.isEmpty()) {
            for (info in listTaskInfo) {
                if (info != null && info.pid == pid) {
                    return info.processName
                }
            }
        }
        return null
    }

    /**
     * 是否是系统main activity
     *
     * @return boolean
     */
    fun isMainLaunchActivity(activity: Activity): Boolean {
        val packageManager = activity.application.packageManager
        val intent = packageManager.getLaunchIntentForPackage(activity.packageName) ?: return false
        val launchComponentName = intent.component
        val componentName = activity.componentName
        return if (launchComponentName != null && componentName.toString() == launchComponentName.toString()) {
            true
        } else false
    }

    /**
     * 是否是系统启动第一次调用mainActivity 页面回退不算
     *
     * @return boolean
     */
    fun isOnlyFirstLaunchActivity(activity: Activity): Boolean {
        val isMainActivity = isMainLaunchActivity(activity)
        val activityLifecycleInfo = DokitConstant.ACTIVITY_LIFECYCLE_INFOS[activity.javaClass.canonicalName]
        return activityLifecycleInfo != null && isMainActivity && !activityLifecycleInfo.isInvokeStopMethod
    }

    /**
     * 打开开发者模式界面 https://blog.csdn.net/ouzhuangzhuang/article/details/84029295
     */
    fun startDevelopmentActivity(context: Context) {
        try {
            val intent = Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
        } catch (e: Exception) {
            try {
                val componentName = ComponentName("com.android.settings", "com.android.settings.DevelopmentSettings")
                val intent = Intent()
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                intent.component = componentName
                intent.action = "android.intent.action.View"
                context.startActivity(intent)
            } catch (e1: Exception) {
                try {
                    //部分小米手机采用这种方式跳转
                    val intent = Intent("com.android.settings.APPLICATION_DEVELOPMENT_SETTINGS")
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    context.startActivity(intent)
                } catch (e2: Exception) {
                    e2.printStackTrace()
                }
            }
        }
    }

    /**
     * 打开系统语言设置页面
     *
     * @param context
     */
    fun startLocalActivity(context: Context) {
        try {
            val intent = Intent(Settings.ACTION_LOCALE_SETTINGS)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 打开正在运行的服务界面 https://www.jianshu.com/p/dd491235d113
     */
    fun startServiceRunningActivity(context: Context) {
        try {
            val componentName: ComponentName
            componentName = if (brand.equals(PHONE_VIVO, ignoreCase = true)) {
                ComponentName("com.android.settings", "com.vivo.settings.VivoSubSettingsForImmersiveBar")
            } else {
                ComponentName("com.android.settings", "com.android.settings.CleanSubSettings")
            }
            val intent = Intent()
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            intent.component = componentName
            intent.action = "android.intent.action.View"
            context.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    val brand: String
        get() = Build.BRAND

    /**
     * 手机品牌
     */
    // 小米
    const val PHONE_XIAOMI = "xiaomi"

    // 华为
    const val PHONE_HUAWEI = "HUAWEI"

    // 华为
    const val PHONE_HONOR = "HONOR"

    // 魅族
    const val PHONE_MEIZU = "Meizu"

    // 索尼
    const val PHONE_SONY = "sony"

    // 三星
    const val PHONE_SAMSUNG = "samsung"

    // LG
    const val PHONE_LG = "lg"

    // HTC
    const val PHONE_HTC = "htc"

    // NOVA
    const val PHONE_NOVA = "nova"

    // OPPO
    const val PHONE_OPPO = "oppo"

    // vivo
    const val PHONE_VIVO = "vivo"

    // 乐视
    const val PHONE_LeMobile = "LeMobile"

    // 联想
    const val PHONE_LENOVO = "lenovo"
}