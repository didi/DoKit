package com.didichuxing.doraemonkit.kit.gpsmock;

import android.location.Location;

/**
 * Created by wanglikun on 2018/12/18.
 */

public class GpsMockManager {
    private static final String TAG = "GpsMockManager";

    private static double mLatitude = -1;
    private static double mLongitude = -1;

    private static boolean isMocking;

    private static boolean mockAMapNavLocation = false;

    private GpsMockManager() {
    }

    public static GpsMockManager getInstance() {
        return Holder.INSTANCE;
    }

    public void startMock() {
        isMocking = true;
    }

    public void stopMock() {
        isMocking = false;
    }

    private void mockLocation(double latitude, double longitude) {
        mLatitude = latitude;
        mLongitude = longitude;
    }

    public void mockLocationWithNotify(double latitude, double longitude) {
        mockLocation(latitude, longitude);
        mockLocationWithNotify(new LocationBuilder().setLatitude(latitude)
                .setLongitude(longitude)
                .setTime(System.currentTimeMillis())
                .build());
    }

    public void mockLocationWithNotify(Location location) {
        if (location == null) return;
        mockLocation(location.getLatitude(), location.getLongitude());
        GpsMockProxyManager.INSTANCE.mockLocationWithNotify(location);
    }

    public boolean isMocking() {
        return isMocking && mLongitude != -1 && mLatitude != -1;
    }

    public double getLatitude() {
        return mLatitude;
    }

    public double getLongitude() {
        return mLongitude;
    }

    public boolean isMockEnable() {
        return ServiceHookManager.getInstance().isHookSuccess();
    }

    private static class Holder {

        private static GpsMockManager INSTANCE = new GpsMockManager();
    }
    public static boolean mockAMapNavLocation() {
        return mockAMapNavLocation;
    }

    public static void setMockAMapNavLocation(boolean mockAMapNavLocation) {
        GpsMockManager.mockAMapNavLocation = mockAMapNavLocation;
    }
}