package com.didichuxing.doraemonkit.config;

import com.didichuxing.doraemonkit.constant.SharedPrefsKey;
import com.didichuxing.doraemonkit.util.DoKitSPUtil;

public class CrashCaptureConfig {
    public static boolean isCrashCaptureOpen() {
        return DoKitSPUtil.getBoolean(SharedPrefsKey.CRASH_CAPTURE_OPEN, false);
    }

    public static void setCrashCaptureOpen(boolean open) {
        DoKitSPUtil.putBoolean(SharedPrefsKey.CRASH_CAPTURE_OPEN, open);
    }
}
