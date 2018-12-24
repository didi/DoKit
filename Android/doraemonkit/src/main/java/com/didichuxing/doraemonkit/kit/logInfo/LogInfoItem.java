package com.didichuxing.doraemonkit.kit.logInfo;

import android.util.Log;

/**
 * Created by wanglikun on 2018/11/13.
 */

public class LogInfoItem {
    public int level = Log.VERBOSE;
    public String date;
    public String time;
    public String packagePriority;
    public String tag;
    public String meseage;
    public String orginalLog;
    public boolean showFull = false;

    public LogInfoItem(String log) {
        orginalLog = log;
        if (log.contains("V/")) {
            level = Log.VERBOSE;
        } else if (log.contains("D/")) {
            level = Log.DEBUG;
        } else if (log.contains("I/")) {
            level = Log.INFO;
        } else if (log.contains("W/")) {
            level = Log.WARN;
        } else if (log.contains("E/")) {
            level = Log.ERROR;
        } else if (log.contains("A/")) {
            level = Log.ASSERT;
        }
        int beginIndex = log.indexOf(": ");
        if (beginIndex == -1) {
            meseage = log;
        } else {
            meseage = log.substring(beginIndex + 2);
        }
        beginIndex = log.indexOf("/");
        int endIndex = log.indexOf("/", beginIndex + 1);
        if (beginIndex != -1 && endIndex != -1) {
            packagePriority = log.substring(beginIndex + 1, endIndex - 3);
        }
        endIndex = log.indexOf(" ");
        if (endIndex != -1) {
            date = log.substring(0, endIndex);
        }
        beginIndex = endIndex;
        endIndex = log.indexOf(" ", beginIndex + 1);
        if (endIndex != -1 && beginIndex != -1) {
            time = log.substring(beginIndex, endIndex);
        }
    }
}