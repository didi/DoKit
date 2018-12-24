package com.didichuxing.doraemonkit.kit.blockmonitor.core;

import android.app.ActivityManager;
import android.content.Context;
import android.text.TextUtils;

import com.didichuxing.doraemonkit.kit.blockmonitor.bean.BlockInfo;

import java.util.List;

public final class BlockCanaryUtils {

    private static String sProcessName;
    private static boolean sProcessNameFirstGetFlag = false;

    private static final String CURRENT_PACKAGE = "com.didichuxing.doraemonkit";

    public static String concernStackString(Context context, BlockInfo blockInfo) {
        String result = "";
        for (String stackEntry : blockInfo.threadStackEntries) {
            if (!TextUtils.isEmpty(stackEntry)) {
                String[] lines = stackEntry.split(BlockInfo.SEPARATOR);
                for (String line : lines) {
                    String keyStackString = concernStackString(context, line);
                    if (keyStackString != null) {
                        return keyStackString;
                    }
                }
                return classSimpleName(lines[0]);
            }
        }
        return result;
    }

    public static boolean isBlockInfoValid(BlockInfo blockInfo) {
        boolean isValid = !TextUtils.isEmpty(blockInfo.timeStart);
        isValid = isValid && blockInfo.timeCost >= 0;
        return isValid;
    }


    private static String concernStackString(Context context, String line) {
        if (!sProcessNameFirstGetFlag) {
            sProcessNameFirstGetFlag = true;
            sProcessName = obtainProcessName(context);
        }
        if (line.startsWith(sProcessName) || line.startsWith(CURRENT_PACKAGE)) {
            return classSimpleName(line);
        }
        return null;
    }

    private static String classSimpleName(String stackLine) {
        int index1 = stackLine.indexOf('(');
        int index2 = stackLine.indexOf(')');
        if (index1 >= 0 && index2 >= 0) {
            return stackLine.substring(index1 + 1, index2);
        }
        return stackLine;
    }

    private static String obtainProcessName(Context context) {
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
