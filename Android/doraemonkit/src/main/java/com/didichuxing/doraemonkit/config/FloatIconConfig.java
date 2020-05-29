package com.didichuxing.doraemonkit.config;

import com.didichuxing.doraemonkit.constant.SharedPrefsKey;
import com.didichuxing.doraemonkit.util.SharedPrefsUtil;

/**
 * Created by wanglikun on 2018/12/14.
 */

public class FloatIconConfig {

    public static int getLastPosX() {
        return SharedPrefsUtil.getInt(SharedPrefsKey.FLOAT_ICON_POS_X, 0);
    }

    public static int getLastPosY() {
        return SharedPrefsUtil.getInt(SharedPrefsKey.FLOAT_ICON_POS_Y, 0);
    }

    public static void saveLastPosY(int val) {
        SharedPrefsUtil.putInt(SharedPrefsKey.FLOAT_ICON_POS_Y, val);
    }

    public static void saveLastPosX(int val) {
        SharedPrefsUtil.putInt(SharedPrefsKey.FLOAT_ICON_POS_X, val);
    }
}
