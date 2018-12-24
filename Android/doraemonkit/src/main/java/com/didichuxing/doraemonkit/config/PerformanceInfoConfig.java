package com.didichuxing.doraemonkit.config;

import android.content.Context;

import com.didichuxing.doraemonkit.constant.SharedPrefsKey;
import com.didichuxing.doraemonkit.util.SharedPrefsUtil;

/**
 * Created by wanglikun on 2018/9/14.
 */

public class PerformanceInfoConfig {
    public static boolean isFPSOpen(Context context) {
        return false;
//        return SharedPrefsUtil.getBoolean(context, SharedPrefsKey.FRAME_INFO_FPS_OPEN, false);
    }

    public static void setFPSOpen(Context context, boolean open) {
        SharedPrefsUtil.putBoolean(context, SharedPrefsKey.FRAME_INFO_FPS_OPEN, open);
    }

    public static boolean isCPUOpen(Context context) {
        return false;
//        return SharedPrefsUtil.getBoolean(context, SharedPrefsKey.FRAME_INFO_CPU_OPEN, false);
    }

    public static void setCPUOpen(Context context, boolean open) {
        SharedPrefsUtil.putBoolean(context, SharedPrefsKey.FRAME_INFO_CPU_OPEN, open);
    }

    public static boolean isMemoryOpen(Context context) {
        return false;
//        return SharedPrefsUtil.getBoolean(context, SharedPrefsKey.FRAME_INFO_MEMORY_OPEN, false);
    }

    public static void setMemoryOpen(Context context, boolean open) {
        SharedPrefsUtil.putBoolean(context, SharedPrefsKey.FRAME_INFO_MEMORY_OPEN, open);
    }
}
