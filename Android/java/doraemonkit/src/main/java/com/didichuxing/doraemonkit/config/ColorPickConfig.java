package com.didichuxing.doraemonkit.config;

import com.didichuxing.doraemonkit.constant.SharedPrefsKey;
import com.didichuxing.doraemonkit.util.SharedPrefsUtil;

/**
 * @author wanglikun
 */
public class ColorPickConfig {
    public static boolean isColorPickOpen() {
        return SharedPrefsUtil.getBoolean(SharedPrefsKey.COLOR_PICK_OPEN, false);
    }

    public static void setColorPickOpen(boolean open) {
        SharedPrefsUtil.putBoolean(SharedPrefsKey.COLOR_PICK_OPEN, open);
    }
}