package com.didichuxing.doraemonkit.util;

/**
 * Created by wanglikun on 2018/9/10.
 */

public class LogHelper {
    private static final String TAG = "DoKit";
    private static boolean IS_DEBUG = true;

    public static void d(String subTag, String msg) {
        if (!IS_DEBUG) {
            return;
        }
        LogUtils.d("[" + subTag + "]: " + msg);
    }

    public static void i(String subTag, String msg) {
        if (!IS_DEBUG) {
            return;
        }
        LogUtils.i("[" + subTag + "]: " + msg);
    }

    public static void e(String subTag, String msg) {
        if (!IS_DEBUG) {
            return;
        }
        LogUtils.e("[" + subTag + "]: " + msg);
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
