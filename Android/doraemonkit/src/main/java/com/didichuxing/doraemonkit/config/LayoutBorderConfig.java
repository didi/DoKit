package com.didichuxing.doraemonkit.config;

import android.content.Context;

import com.didichuxing.doraemonkit.constant.SharedPrefsKey;
import com.didichuxing.doraemonkit.util.SharedPrefsUtil;

/**
 * Created by wanglikun on 2019/1/7
 */
public class LayoutBorderConfig {
    public static boolean isLayoutBorderOpen(Context context) {
        return SharedPrefsUtil.getBoolean(context, SharedPrefsKey.LAYOUT_BORDER_OPEN, false);
    }

    public static void setLayoutBorderOpen(Context context, boolean open) {
        SharedPrefsUtil.putBoolean(context, SharedPrefsKey.LAYOUT_BORDER_OPEN, open);
    }

    public static boolean isLayoutLevelOpen(Context context) {
        return SharedPrefsUtil.getBoolean(context, SharedPrefsKey.LAYOUT_LEVEL_OPEN, false);
    }

    public static void setLayoutLevelOpen(Context context, boolean open) {
        SharedPrefsUtil.putBoolean(context, SharedPrefsKey.LAYOUT_LEVEL_OPEN, open);
    }
}