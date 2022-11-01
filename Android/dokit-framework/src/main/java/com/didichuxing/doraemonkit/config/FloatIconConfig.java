package com.didichuxing.doraemonkit.config;

import com.didichuxing.doraemonkit.constant.SharedPrefsKey;
import com.didichuxing.doraemonkit.util.DoKitSPUtil;

/**
 * Created by wanglikun on 2018/12/14.
 */

public class FloatIconConfig {

    public static int getLastPosX() {
        return DoKitSPUtil.getInt(SharedPrefsKey.FLOAT_ICON_POS_X, 0);
    }

    public static int getLastPosY() {
        return DoKitSPUtil.getInt(SharedPrefsKey.FLOAT_ICON_POS_Y, 0);
    }

    public static void saveLastPosY(int val) {
        DoKitSPUtil.putInt(SharedPrefsKey.FLOAT_ICON_POS_Y, val);
    }

    public static void saveLastPosX(int val) {
        DoKitSPUtil.putInt(SharedPrefsKey.FLOAT_ICON_POS_X, val);
    }
}
