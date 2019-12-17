package com.didichuxing.doraemonkit.ui.base;

import android.app.Activity;
import android.os.Bundle;

/**
 * Created by jintai on 2019/9/16.
 * dokitView intent
 */

public class DokitIntent {
    public static final int MODE_NORMAL = 0;

    public static final int MODE_SINGLE_INSTANCE = 1;

    public Class<? extends Object> targetClass;

    public Bundle bundle;
    /**
     * 赋值为类名 不接受对外赋值
     */
    private String tag;

    public String getTag() {
        return tag;
    }

    public Activity activity;

    public int mode = MODE_NORMAL;


    public DokitIntent(Class<? extends Object> targetClass) {

    }

}
