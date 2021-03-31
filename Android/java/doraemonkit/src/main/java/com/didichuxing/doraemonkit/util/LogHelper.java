package com.didichuxing.doraemonkit.util;

import com.didichuxing.doraemonkit.util.LogUtils;

/**
 * Created by wanglikun on 2018/9/10.
 */

public class LogHelper {
    private static final String TAG = "DoraemonKit";
    private static boolean IS_DEBUG = true;

    public static void d(String subTag, String msg) {
        if (!IS_DEBUG) {
            return;
        }
        LogUtils.v("[" + subTag + "]: " + msg);
    }

    public static void i(String subTag, String msg) {
        if (!IS_DEBUG) {
            return;
        }
        LogUtils.v("[" + subTag + "]: " + msg);
    }

    public static void e(String subTag, String msg) {
        if (!IS_DEBUG) {
            return;
        }
        LogUtils.v("[" + subTag + "]: " + msg);
    }

    public static void json(String subTag, Object o) {
        if (!IS_DEBUG) {
            return;
        }
        LogUtils.json("[" + subTag + "]: ", o);
    }

    public static void setDebug(boolean debug) {
        IS_DEBUG = debug;
    }
}
