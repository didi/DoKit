package com.didichuxing.doraemonkit.config;

import com.didichuxing.doraemonkit.constant.SharedPrefsKey;
import com.didichuxing.doraemonkit.util.DoKitSPUtil;

/**
 * @author wanglikun
 */
public class AlignRulerConfig {
    public static boolean isAlignRulerOpen() {
        return DoKitSPUtil.getBoolean(SharedPrefsKey.ALIGN_RULER_OPEN, false);
    }

    public static void setAlignRulerOpen(boolean open) {
        DoKitSPUtil.putBoolean(SharedPrefsKey.ALIGN_RULER_OPEN, open);
    }
}