package com.didichuxing.doraemonkit.config;

import com.didichuxing.doraemonkit.constant.SharedPrefsKey;
import com.didichuxing.doraemonkit.util.SharedPrefsUtil;

/**
 * @author wanglikun
 */
public class AlignRulerConfig {
    public static boolean isAlignRulerOpen() {
        return SharedPrefsUtil.getBoolean(SharedPrefsKey.ALIGN_RULER_OPEN, false);
    }

    public static void setAlignRulerOpen(boolean open) {
        SharedPrefsUtil.putBoolean(SharedPrefsKey.ALIGN_RULER_OPEN, open);
    }
}