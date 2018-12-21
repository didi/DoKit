package com.didichuxing.doraemonkit.ui.base;

import android.os.Bundle;

/**
 * Created by wanglikun on 2018/10/25.
 */

public class PageIntent {
    public static final int MODE_NORMAL = 0;

    public static final int MODE_SINGLE_INSTANCE = 1;

    public Class<? extends BaseFloatPage> targetClass;

    public Bundle bundle;

    public String tag;

    public int mode = MODE_NORMAL;

    public PageIntent() {
    }

    public PageIntent(Class<? extends BaseFloatPage> targetClass) {
        this.targetClass = targetClass;
    }
}
