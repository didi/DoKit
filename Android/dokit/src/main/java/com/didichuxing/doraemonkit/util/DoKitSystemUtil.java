package com.didichuxing.doraemonkit.util;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.text.TextUtils;

import com.didichuxing.doraemonkit.kit.core.DoKitManager;
import com.didichuxing.doraemonkit.kit.core.ActivityLifecycleStatusInfo;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by wanglikun on 2018/12/21.
 */

public class DoKitSystemUtil {
    private static final String TAG = "SystemUtil";

    private static final Pattern VERSION_NAME_PATTERN = Pattern.compile("(\\d+\\.\\d+\\.\\d+)-*.*");
    private static String sAppVersion;
    private static int sAppVersionCode = -1;
    private static String sPackageName;
    private static String sAppName;

    public static String getVersionName(Context context) {
        if (!TextUtils.isEmpty(sAppVersion)) {
            return sAppVersion;
        } else {
            String appVersion = "";
            try {
                String pkgName = context.getApplicationInfo().packageName;
                appVersion = context.getPackageManager().getPackageInfo(pkgName, 0).versionName;
                if (appVersion != null && appVersion.length() > 0) {
                    Matcher matcher = VERSION_NAME_PATTERN.matcher(appVersion);
                    if (matcher.matches()) {
                        appVersion = matcher.group(1);
                        sAppVersion = appVersion;
                    }
                }
            } catch (Throwable t) {
                LogHelper.e(TAG, t.toString());
            }

            return appVersion;
        }
    }

    public static int getVersionCode(Context context) {
        if (sAppVersionCode != -1) {
            return sAppVersionCode;
        } else {
            int versionCode;

            try {
                String pkgName = context.getApplicationInfo().packageName;
                versionCode = context.getPackageManager().getPackageInfo(pkgName, 0).versionCode;
                if (versionCode != -1) {
                    sAppVersionCode = versionCode;
                }
            } catch (Throwable t) {
                LogHelper.e(TAG, t.toString());
            }

            return sAppVersionCode;
        }
    }

    public static String getPackageName(Context context) {
        if (TextUtils.isEmpty(sPackageName)) {
            sPackageName = context.getPackageName();
        }
        return sPackageName;
    }

    public static String getAppName(Context context) {
        if (!TextUtils.isEmpty(sAppName)) {
            return sAppName;
        } else {
            PackageManager packageManager = null;
            ApplicationInfo applicationInfo = null;
            packageManager = context.getPackageManager();
            try {
                applicationInfo = packageManager.getApplicationInfo(context.getPackageName(), 0);
            } catch (PackageManager.NameNotFoundException e) {
                LogHelper.e(TAG, e.toString());
            }
            sAppName = (String) packageManager.getApplicationLabel(applicationInfo);
            return sAppName;
        }
    }

    public static String obtainProcessName(Context context) {
        final int pid = android.os.Process.myPid();
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> listTaskInfo = am.getRunningAppProcesses();
        if (listTaskInfo != null && !listTaskInfo.isEmpty()) {
            for (ActivityManager.RunningAppProcessInfo info : listTaskInfo) {
                if (info != null && info.pid == pid) {
                    return info.processName;
                }
            }
        }
        return null;
    }


    /**
     * 是否是系统main activity
     *
     * @return boolean
     */
    public static boolean isMainLaunchActivity(Activity activity) {
        PackageManager packageManager = activity.getApplication().getPackageManager();
        Intent intent = packageManager.getLaunchIntentForPackage(activity.getPackageName());
        if (intent == null) {
            return false;
        }
        ComponentName launchComponentName = intent.getComponent();
        ComponentName componentName = activity.getComponentName();
        if (launchComponentName != null && componentName.toString().equals(launchComponentName.toString())) {
            return true;
        }
        return false;
    }


    /**
     * 是否是系统启动第一次调用mainActivity 页面回退不算
     *
     * @return boolean
     */
    public static boolean isOnlyFirstLaunchActivity(Activity activity) {
        boolean isMainActivity = isMainLaunchActivity(activity);
        ActivityLifecycleStatusInfo activityLifecycleInfo = DoKitManager.INSTANCE.getACTIVITY_LIFECYCLE_INFOS().get(activity.getClass().getCanonicalName());
        return activityLifecycleInfo != null && isMainActivity && !activityLifecycleInfo.isInvokeStopMethod();
    }


    /**
     * 打开开发者模式界面 https://blog.csdn.net/ouzhuangzhuang/article/details/84029295
     */
    public static void startDevelopmentActivity(Context context) {
        try {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception e) {
            try {
                ComponentName componentName = new ComponentName("com.android.settings", "com.android.settings.DevelopmentSettings");
                Intent intent = new Intent();
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setComponent(componentName);
                intent.setAction("android.intent.action.View");
                context.startActivity(intent);
            } catch (Exception e1) {
                try {
                    //部分小米手机采用这种方式跳转
                    Intent intent = new Intent("com.android.settings.APPLICATION_DEVELOPMENT_SETTINGS");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                } catch (Exception e2) {
                    e2.printStackTrace();
                }

            }
        }
    }


    /**
     * 打开系统语言设置页面
     *
     * @param context
     */
    public static void startLocalActivity(Context context) {
        try {
            Intent intent = new Intent(Settings.ACTION_LOCALE_SETTINGS);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 打开正在运行的服务界面 https://www.jianshu.com/p/dd491235d113
     */
    public static void startServiceRunningActivity(Context context) {
        try {
            ComponentName componentName;
            if (getBrand().equalsIgnoreCase(PHONE_VIVO)) {
                componentName = new ComponentName("com.android.settings", "com.vivo.settings.VivoSubSettingsForImmersiveBar");
            } else {
                componentName = new ComponentName("com.android.settings", "com.android.settings.CleanSubSettings");
            }
            Intent intent = new Intent();
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setComponent(componentName);
            intent.setAction("android.intent.action.View");
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static String getBrand() {
        return android.os.Build.BRAND;
    }

    /**
     * 手机品牌
     */
    // 小米
    public static final String PHONE_XIAOMI = "xiaomi";
    // 华为
    public static final String PHONE_HUAWEI = "HUAWEI";
    // 华为
    public static final String PHONE_HONOR = "HONOR";
    // 魅族
    public static final String PHONE_MEIZU = "Meizu";
    // 索尼
    public static final String PHONE_SONY = "sony";
    // 三星
    public static final String PHONE_SAMSUNG = "samsung";
    // LG
    public static final String PHONE_LG = "lg";
    // HTC
    public static final String PHONE_HTC = "htc";
    // NOVA
    public static final String PHONE_NOVA = "nova";
    // OPPO
    public static final String PHONE_OPPO = "oppo";
    // vivo
    public static final String PHONE_VIVO = "vivo";
    // 乐视
    public static final String PHONE_LeMobile = "LeMobile";
    // 联想
    public static final String PHONE_LENOVO = "lenovo";


}
