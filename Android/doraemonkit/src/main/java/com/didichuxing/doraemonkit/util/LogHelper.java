package com.didichuxing.doraemonkit.util;

import com.blankj.utilcode.util.LogUtils;

/**
 * Created by wanglikun on 2018/9/10.
 */

public class LogHelper {
    private static final String TAG = "Doraemon";

    public static void d(String subTag, String msg) {
        LogUtils.v("[" + subTag + "]: " + msg);
    }

    public static void i(String subTag, String msg) {
        LogUtils.v("[" + subTag + "]: " + msg);
    }

    public static void e(String subTag, String msg) {
        LogUtils.v("[" + subTag + "]: " + msg);
    }

    public static void setDebug(boolean debug) {
        //DEBUG = debug;
    }
}
