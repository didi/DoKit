package com.didichuxing.doraemonkit.kit.gpsmock;

/**
 * Created by wanglikun on 2018/12/18.
 */

public class GpsMockManager {
    private static final String TAG = "GpsMockManager";

    private static double mLatitude = -1;
    private static double mLongitude = -1;

    private static boolean isMocking;

    public void startMock() {
        isMocking = true;
    }

    public void stopMock() {
        isMocking = false;
    }

    private static class Holder {
        private static GpsMockManager INSTANCE = new GpsMockManager();
    }

    public static GpsMockManager getInstance() {
        return Holder.INSTANCE;
    }

    private GpsMockManager() {
    }

    public void mockLocation(double latitude, double longitude) {
        mLatitude = latitude;
        mLongitude = longitude;
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
}