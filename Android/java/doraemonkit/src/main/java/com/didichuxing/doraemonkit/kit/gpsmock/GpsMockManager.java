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

    public void mockLocation(double latitude, double longitude) {
        mLatitude = latitude;
        mLongitude = longitude;
    }

    public void mockLocationWithNotify(double latitude, double longitude) {
        mockLocation(latitude, longitude);
        // TODO: 1/22/21 notify Location Listeners
        mockLocationWithNotify(new LocationBuilder().setLatitude(latitude).setLongitude(longitude).build());
    }

    public void mockLocationWithNotify(Location location) {
        if (location == null) return;
        mockLocation(location.getLatitude(), location.getLongitude());
        GpsMockProxyManager.getInstance().mockLocationWithNotify(location);
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

}