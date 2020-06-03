package com.didichuxing.doraemonkit.config;

import com.blankj.utilcode.util.SPUtils;
import com.didichuxing.doraemonkit.constant.SharedPrefsKey;

/**
 * Created by jint on 2018/12/14.
 */

public class FloatIconConfig {

    public static int getLastPosX() {
        return SPUtils.getInstance().getInt(SharedPrefsKey.FLOAT_ICON_POS_X, 0);
    }

    public static int getLastPosY() {
        return SPUtils.getInstance().getInt(SharedPrefsKey.FLOAT_ICON_POS_Y, 0);
    }

    public static void saveLastPosY(int val) {
        SPUtils.getInstance().put(SharedPrefsKey.FLOAT_ICON_POS_Y, val);
    }

    public static void saveLastPosX(int val) {
        SPUtils.getInstance().put(SharedPrefsKey.FLOAT_ICON_POS_X, val);
    }
}
