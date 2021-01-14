package com.didichuxing.doraemonkit.config;

import com.didichuxing.doraemonkit.constant.SharedPrefsKey;
import com.didichuxing.doraemonkit.util.SharedPrefsUtil;

/**
 * @author wanglikun
 */
public class LogInfoConfig {
    public static boolean isLogInfoOpen() {
        return SharedPrefsUtil.getBoolean(SharedPrefsKey.LOG_INFO_OPEN, false);
    }

    public static void setLogInfoOpen(boolean open) {
        SharedPrefsUtil.putBoolean(SharedPrefsKey.LOG_INFO_OPEN, open);
    }
}