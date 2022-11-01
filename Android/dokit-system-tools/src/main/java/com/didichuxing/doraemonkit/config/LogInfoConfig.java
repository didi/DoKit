package com.didichuxing.doraemonkit.config;

import com.didichuxing.doraemonkit.constant.SharedPrefsKey;
import com.didichuxing.doraemonkit.util.DoKitSPUtil;

/**
 * @author wanglikun
 */
public class LogInfoConfig {
    public static boolean isLogInfoOpen() {
        return DoKitSPUtil.getBoolean(SharedPrefsKey.LOG_INFO_OPEN, false);
    }

    public static void setLogInfoOpen(boolean open) {
        DoKitSPUtil.putBoolean(SharedPrefsKey.LOG_INFO_OPEN, open);
    }
}