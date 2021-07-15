package com.didichuxing.doraemonkit.util;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.Nullable;

import com.didichuxing.doraemonkit.util.AppUtils;
import com.didichuxing.doraemonkit.util.Utils;
import com.didichuxing.doraemonkit.constant.SharedPrefsKey;

import static com.didichuxing.doraemonkit.constant.SharedPrefsKey.APP_HEALTH;

/**
 * Created by wanglikun on 2018/9/14.
 */

public class DoKitSPUtil {
    /**
     * 退出时保存sp状态 需要用commit
     */
    private static final String SHARED_PREFS_DORAEMON = "shared_prefs_doraemon";

    private static SharedPreferences getSharedPrefs() {
        return getSharedPrefs(SHARED_PREFS_DORAEMON);
    }

    @Nullable
    public static SharedPreferences getSharedPrefs(String name) {
        return Utils.getApp().getSharedPreferences(name, Context.MODE_PRIVATE);
    }

    public static String getString(String key, String defVal) {
        return getSharedPrefs().getString(key, defVal);
    }

    public static void putString(String key, String value) {
        putString(SHARED_PREFS_DORAEMON, key, value);
    }

    public static void putString(String table, String key, String value) {
        try {
            if (getSharedPrefs(table) != null) {
                if (key.equals(SHARED_PREFS_DORAEMON)) {
                    getSharedPrefs(table).edit().putString(key, value).commit();
                } else {
                    getSharedPrefs(table).edit().putString(key, value).apply();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void putBoolean(String key, boolean value) {

        putBoolean(SHARED_PREFS_DORAEMON, key, value);
    }

    public static void putBoolean(String table, String key, boolean value) {
        try {
            if (getSharedPrefs(table) != null) {
                if (key.equals(SharedPrefsKey.APP_HEALTH)) {
                    getSharedPrefs(table).edit().putBoolean(key, value).commit();
                } else {
                    getSharedPrefs(table).edit().putBoolean(key, value).apply();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean getBoolean(String key, boolean defVal) {
        return getSharedPrefs().getBoolean(key, defVal);
    }

    public static void putInt(String key, int value) {
        putInt(SHARED_PREFS_DORAEMON, key, value);
    }

    public static void putInt(String table, String key, Integer value) {
        try {
            getSharedPrefs(table).edit().putInt(key, value).apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int getInt(String key, int defVal) {
        return getSharedPrefs().getInt(key, defVal);
    }

    public static void putFloat(String table, String key, Float value) {
        try {
            getSharedPrefs(table).edit().putFloat(key, value).apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void putFloat(String key, Float value) {
        try {
            getSharedPrefs(SHARED_PREFS_DORAEMON).edit().putFloat(key, value).apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static float getFloat(String key, Float value) {
        return getSharedPrefs().getFloat(key, value);
    }

    public static void putLong(String table, String key, Long value) {
        try {
            getSharedPrefs(table).edit().putLong(key, value).apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
