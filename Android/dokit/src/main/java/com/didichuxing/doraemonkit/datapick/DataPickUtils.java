package com.didichuxing.doraemonkit.datapick;

import android.app.Activity;

import com.didichuxing.doraemonkit.util.ActivityUtils;

/**
 * didi Create on 2022/7/14 .
 * <p>
 * Copyright (c) 2022/7/14 by didiglobal.com.
 *
 * @author <a href="realonlyone@126.com">zhangjun</a>
 * @version 1.0
 * @Date 2022/7/14 2:57 下午
 * @Description 用一句话说明文件功能
 */

public class DataPickUtils {


    private static String mDoKitHomeClickPage = "";

    private DataPickUtils() {
    }

    public static String getCurrentPage() {
        Activity activity = ActivityUtils.getTopActivity();
        if (activity != null) {
            return activity.getClass().getName();
        }
        return "";
    }


    public static void setDoKitHomeClickPage(String pageId) {
        mDoKitHomeClickPage = pageId;
    }

    public static String getDoKitHomeClickPage() {
        return mDoKitHomeClickPage;
    }
}
