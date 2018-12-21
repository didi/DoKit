package com.didichuxing.doraemonkit.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by wanglikun on 2018/9/14.
 */

public class SharedPrefsUtil {
    private static final String SHARED_PREFS_DORAEMON = "shared_prefs_doraemon";

    private static SharedPreferences getSharedPrefs(Context context) {
        return context.getSharedPreferences(SHARED_PREFS_DORAEMON, Context.MODE_PRIVATE);
    }

    public static void putString(Context context, String key, String value) {
        getSharedPrefs(context).edit().putString(key, value).apply();
    }

    public static String getString(Context context,String key, String defVal) {
        return getSharedPrefs(context).getString(key, defVal);
    }

    public static void putBoolean(Context context, String key, boolean value) {
        if (context == null) {
            return;
        }
        getSharedPrefs(context).edit().putBoolean(key, value).apply();
    }

    public static boolean getBoolean(Context context,String key, boolean defVal) {
        return context != null && getSharedPrefs(context).getBoolean(key, defVal);
    }

    public static void putInt(Context context, String key, int value) {
        getSharedPrefs(context).edit().putInt(key, value).apply();
    }

    public static int getInt(Context context,String key, int defVal) {
        return getSharedPrefs(context).getInt(key, defVal);
    }
}
