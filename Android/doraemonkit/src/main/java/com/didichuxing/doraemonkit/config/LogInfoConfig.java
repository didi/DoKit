package com.didichuxing.doraemonkit.config;

import android.content.Context;

import com.didichuxing.doraemonkit.constant.SharedPrefsKey;
import com.didichuxing.doraemonkit.util.SharedPrefsUtil;

/**
 * @author wanglikun
 */
public class LogInfoConfig {
    public static boolean isLogInfoOpen(Context context) {
        return SharedPrefsUtil.getBoolean(context, SharedPrefsKey.LOG_INFO_OPEN, false);
    }

    public static void setLogInfoOpen(Context context, boolean open) {
        SharedPrefsUtil.putBoolean(context, SharedPrefsKey.LOG_INFO_OPEN, open);
    }
}