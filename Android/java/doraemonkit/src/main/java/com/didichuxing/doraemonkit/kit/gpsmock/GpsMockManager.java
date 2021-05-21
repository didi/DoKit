package com.didichuxing.doraemonkit.kit.gpsmock;

import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by wanglikun on 2018/12/18.
 */

public class GpsMockManager {
    private static final String TAG = "GpsMockManager";

    private static double mLatitude = -1;
    private static double mLongitude = -1;

    private static boolean isMocking;

    /**
     * 高德导航SDK 在接收到定位之后，会将定位和路线进行吸附，如果这里也进行mock会造成
     * 1：吸附的结果被覆盖掉
     * 2：冗余定位回调，因为高德在吸附后也会回调给导航接口位置更新，如果我们再次进行更新，就会是冗余的更新
     */
    private static boolean mockAMapNavLocation = false;

    /**
     * 外部可以通过API得知当前定位点是否是mock的，默认不可知，如果有需要可以打开这个配置，这样外部就可以通过
     *
     * @see Location#isFromMockProvider()
     * 得知当前定位是mock的
     */
    private static boolean isFromMockProvider = false;
    private Bundle extras = new Bundle();

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
        mockLocationWithNotify(new LocationBuilder()
                .setLatitude(latitude)
                .setLongitude(longitude)
                .build());
    }

    public void mockLocationWithNotify(Location location) {
        if (location == null) return;
        mockLocation(location.getLatitude(), location.getLongitude());
        location.setProvider(LocationManager.GPS_PROVIDER);
        if (extras.size() == 0) {
            extras.putInt("satellites", 9);
        }
        location.setExtras(extras);

        long currentTimeMillis = System.currentTimeMillis();
        location.setTime(currentTimeMillis);
        location.setElapsedRealtimeNanos(currentTimeMillis);
        if (isFromMockProvider()) {
            Class<? extends Location> locationClass = location.getClass();
            try {
                Method method = locationClass.getMethod("setIsFromMockProvider", boolean.class);
                method.setAccessible(true);
                method.invoke(location, true);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
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

    public static boolean isFromMockProvider() {
        return isFromMockProvider;
    }

    public static void setIsFromMockProvider(boolean isFromMockProvider) {
        GpsMockManager.isFromMockProvider = isFromMockProvider;
    }
}