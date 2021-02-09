package com.didichuxing.doraemonkit.kit.fly.manual;

import android.location.Location;

import com.blankj.utilcode.util.LogUtils;
import com.didichuxing.doraemonkit.kit.fly.preset.FloatGpsPresetMockCache;
import com.didichuxing.doraemonkit.kit.gpsmock.GpsMockManager;

public class FloatGpsMockCache {
    private static final String TAG = "FloatGpsMockCache";
    private static Location sLastMock;
    private static float[] sDistanceAndBearing = new float[2];

    public static void mockToLocation(double latitude, double longitude) {
        LogUtils.d(TAG, "⚠️ mockToLocation() called with: latitude = [" + latitude + "], longitude = [" + longitude + "]");
        FloatGpsPresetMockCache.updateCustomMockLocation(latitude, longitude);

        Location location = new Location("dev mock");
        location.setLatitude(latitude);
        location.setLongitude(longitude);

        if (sLastMock != null) {
            Location.distanceBetween(sLastMock.getLatitude(), sLastMock.getLongitude(), latitude, longitude, sDistanceAndBearing);
            location.setSpeed(sDistanceAndBearing[0]);
            location.setBearing(sDistanceAndBearing[1]);
        }
        sLastMock = location;
        GpsMockManager.getInstance().mockLocationWithNotify(sLastMock);
    }
}
