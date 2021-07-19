package com.didichuxing.doraemonkit.config;

import com.didichuxing.doraemonkit.constant.SharedPrefsKey;
import com.didichuxing.doraemonkit.util.DoKitSPUtil;

/**
 * @author wanglikun
 */
public class ColorPickConfig {
    public static boolean isColorPickOpen() {
        return DoKitSPUtil.getBoolean(SharedPrefsKey.COLOR_PICK_OPEN, false);
    }

    public static void setColorPickOpen(boolean open) {
        DoKitSPUtil.putBoolean(SharedPrefsKey.COLOR_PICK_OPEN, open);
    }
}