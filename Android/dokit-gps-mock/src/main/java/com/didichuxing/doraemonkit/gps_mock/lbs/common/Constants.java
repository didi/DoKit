package com.didichuxing.doraemonkit.gps_mock.lbs.common;

import android.content.Context;

import com.didichuxing.doraemonkit.util.DoKitSPUtil;
import com.google.gson.Gson;

/**
 * @Author: changzuozhen
 * @Date: 2019-10-15 15:37
 */
public final class Constants {
    public static final String TAG = "Constants";
    public static final Gson GSON = new Gson();
    private static final String MOCK_LOCATION_KEY = "mock_location_key";
    private static Context sContext;

    private Constants() {
    }

    public static Context getContext() {
        return sContext;
    }

    public static void setContext(Context context) {
        sContext = context;
    }

    public static String loadLocationConfigJson() {
        return DoKitSPUtil.getString(MOCK_LOCATION_KEY, "");
    }

    public static void saveLocationConfigJson(String locLocalConfigJson) {
        DoKitSPUtil.putString(MOCK_LOCATION_KEY, locLocalConfigJson);
    }
}
