package com.didichuxing.doraemonkit.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;

import com.didichuxing.doraemonkit.constant.SharedPrefsKey;

import static com.didichuxing.doraemonkit.constant.SharedPrefsKey.APP_HEALTH;

/**
 * Created by wanglikun on 2018/9/14.
 */

public class SharedPrefsUtil {
    /**
     * 退出时保存sp状态 需要用commit
     */
    private static final String SHARED_PREFS_DORAEMON = "shared_prefs_doraemon";

    private static SharedPreferences getSharedPrefs(Context context) {
        return getSharedPrefs(context, SHARED_PREFS_DORAEMON);
    }

    @Nullable
    public static SharedPreferences getSharedPrefs(Context context, String name) {
        return context.getSharedPreferences(name, Context.MODE_PRIVATE);
    }

    public static String getString(Context context, String key, String defVal) {
        return getSharedPrefs(context).getString(key, defVal);
    }

    public static void putString(Context context, String key, String value) {
        putString(context, SHARED_PREFS_DORAEMON, key, value);
    }

    public static void putString(Context context, String table, String key, String value) {
        if (key.equals(SHARED_PREFS_DORAEMON)) {
            getSharedPrefs(context, table).edit().putString(key, value).commit();
        } else {
            getSharedPrefs(context, table).edit().putString(key, value).apply();
        }
    }

    public static void putBoolean(Context context, String key, boolean value) {
        if (context == null) {
            return;
        }
        putBoolean(context, SHARED_PREFS_DORAEMON, key, value);
    }

    public static void putBoolean(Context context, String table, String key, boolean value) {
        if (key.equals(SharedPrefsKey.APP_HEALTH)) {
            getSharedPrefs(context, table).edit().putBoolean(key, value).commit();
        } else {
            getSharedPrefs(context, table).edit().putBoolean(key, value).apply();
        }
    }

    public static boolean getBoolean(Context context, String key, boolean defVal) {
        return context != null && getSharedPrefs(context).getBoolean(key, defVal);
    }

    public static void putInt(Context context, String key, int value) {
        putInt(context, SHARED_PREFS_DORAEMON, key, value);
    }

    public static void putInt(Context context, String table, String key, Integer value) {
        getSharedPrefs(context, table).edit().putInt(key, value).apply();
    }

    public static int getInt(Context context, String key, int defVal) {
        return getSharedPrefs(context).getInt(key, defVal);
    }

    public static void putFloat(Context context, String table, String key, Float value) {
        getSharedPrefs(context, table).edit().putFloat(key, value).apply();
    }

    public static void putFloat(Context context, String key, Float value) {
        getSharedPrefs(context, SHARED_PREFS_DORAEMON).edit().putFloat(key, value).apply();
    }

    public static float getFloat(Context context, String key, Float value) {
        return getSharedPrefs(context).getFloat(key, value);
    }

    public static void putLong(Context context, String table, String key, Long value) {
        getSharedPrefs(context, table).edit().putLong(key, value).apply();
    }


}
