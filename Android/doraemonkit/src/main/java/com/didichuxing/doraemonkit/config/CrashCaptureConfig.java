package com.didichuxing.doraemonkit.config;

import android.content.Context;
import android.os.Environment;

import com.didichuxing.doraemonkit.constant.SharedPrefsKey;
import com.didichuxing.doraemonkit.util.SharedPrefsUtil;

public class CrashCaptureConfig {
    public static boolean isCrashCaptureOpen(Context context) {
        return SharedPrefsUtil.getBoolean(context, SharedPrefsKey.CRASH_CAPTURE_OPEN, false);
    }

    public static void setCrashCaptureOpen(Context context, boolean open) {
        SharedPrefsUtil.putBoolean(context, SharedPrefsKey.CRASH_CAPTURE_OPEN, open);
    }
}
