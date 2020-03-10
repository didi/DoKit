package com.didichuxing.doraemonkit.config;

import android.content.Context;

import com.didichuxing.doraemonkit.constant.CachesKey;
import com.didichuxing.doraemonkit.constant.SharedPrefsKey;
import com.didichuxing.doraemonkit.model.LatLng;
import com.didichuxing.doraemonkit.util.CacheUtils;
import com.didichuxing.doraemonkit.util.SharedPrefsUtil;

/**
 * Created by wanglikun on 2018/9/20.
 */

public class GpsMockConfig {
    public static boolean isGPSMockOpen(Context context) {
        return SharedPrefsUtil.getBoolean(context, SharedPrefsKey.GPS_MOCK_OPEN, false);
    }

    public static void setGPSMockOpen(Context context, boolean open) {
        SharedPrefsUtil.putBoolean(context, SharedPrefsKey.GPS_MOCK_OPEN, open);
    }

    public static LatLng getMockLocation(Context context) {
        return (LatLng) CacheUtils.readObject(context, CachesKey.MOCK_LOCATION);
    }

    public static void saveMockLocation(Context context, LatLng latLng) {
        CacheUtils.saveObject(context, CachesKey.MOCK_LOCATION, latLng);
    }
}