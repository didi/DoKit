package com.didichuxing.doraemonkit.util;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by wanglikun on 2018/12/21.
 */

public class SystemUtil {
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
}
