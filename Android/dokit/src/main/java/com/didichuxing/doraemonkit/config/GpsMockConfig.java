package com.didichuxing.doraemonkit.config;

import com.didichuxing.doraemonkit.constant.CachesKey;
import com.didichuxing.doraemonkit.constant.SharedPrefsKey;
import com.didichuxing.doraemonkit.model.LatLng;
import com.didichuxing.doraemonkit.util.DoKitCacheUtils;
import com.didichuxing.doraemonkit.util.DoKitSPUtil;

/**
 * Created by wanglikun on 2018/9/20.
 */

public class GpsMockConfig {
    /**
     * @return
     */
    public static boolean isGPSMockOpen() {
        return DoKitSPUtil.getBoolean(SharedPrefsKey.GPS_MOCK_OPEN, false);
    }

    /**
     * @param open
     */
    public static void setGPSMockOpen(boolean open) {
        DoKitSPUtil.putBoolean(SharedPrefsKey.GPS_MOCK_OPEN, open);
    }

    public static boolean isPosMockOpen() {
        return DoKitSPUtil.getBoolean(SharedPrefsKey.POS_MOCK_OPEN, false);
    }

    public static void putPosMockOpen(boolean open) {
        DoKitSPUtil.putBoolean(SharedPrefsKey.POS_MOCK_OPEN, open);
    }

    public static boolean isRouteMockOpen() {
        return DoKitSPUtil.getBoolean(SharedPrefsKey.ROUTE_MOCK_OPEN, false);
    }

    public static void putRouteMockOpen(boolean open) {
        DoKitSPUtil.putBoolean(SharedPrefsKey.ROUTE_MOCK_OPEN, open);
    }

    public static boolean isRouteDriftMockOpen() {
        return DoKitSPUtil.getBoolean(SharedPrefsKey.ROUTE_DRIFT_MOCK_OPEN, false);
    }

    public static void putRouteDriftMockOpen(boolean open) {
        DoKitSPUtil.putBoolean(SharedPrefsKey.ROUTE_DRIFT_MOCK_OPEN, open);
    }

    public static boolean isRouteDriftMockLostLocOpen() {
        return DoKitSPUtil.getBoolean(SharedPrefsKey.ROUTE_DRIFT_MOCK_LOST_LOC_OPEN, false);
    }

    public static void putRouteDriftMockLostLocOpen(boolean open) {
        DoKitSPUtil.putBoolean(SharedPrefsKey.ROUTE_DRIFT_MOCK_LOST_LOC_OPEN, open);
    }

    public static void putRouteMockSpeed(float speed) {
        DoKitSPUtil.putFloat(SharedPrefsKey.ROUTE_MOCK_SPEED, speed);
    }

    public static float getRouteMockSpeed() {
        return DoKitSPUtil.getFloat(SharedPrefsKey.ROUTE_MOCK_SPEED, 60.0f);
    }

    public static void putRouteMockAccuracy(int accuracy) {
        DoKitSPUtil.putInt(SharedPrefsKey.ROUTE_MOCK_ACCURACY, accuracy);
    }

    public static int getRouteMockAccuracy() {
        return DoKitSPUtil.getInt(SharedPrefsKey.ROUTE_MOCK_ACCURACY, 500);
    }

    public static void putRouteMockDriftMode(int index) {
        DoKitSPUtil.putInt(SharedPrefsKey.ROUTE_DRIFT_MODE, index);
    }

    public static int getRouteMockDriftMode() {
        return DoKitSPUtil.getInt(SharedPrefsKey.ROUTE_DRIFT_MODE, 0);
    }

    public static void putRouteMockDriftType(int index) {
        DoKitSPUtil.putInt(SharedPrefsKey.ROUTE_DRIFT_TYPE, index);
    }

    public static int getRouteMockDriftType() {
        return DoKitSPUtil.getInt(SharedPrefsKey.ROUTE_DRIFT_TYPE, 0);
    }

    public static void putSeekBarLow(int progressLow) {
        DoKitSPUtil.putInt(SharedPrefsKey.ROUTE_DRIFT_SEEKBAR_PROGRESS_LOW, progressLow);
    }

    public static int getSeekBarLow() {
        return DoKitSPUtil.getInt(SharedPrefsKey.ROUTE_DRIFT_SEEKBAR_PROGRESS_LOW, 0);
    }

    public static void putSeekBarHigh(int progressLow) {
        DoKitSPUtil.putInt(SharedPrefsKey.ROUTE_DRIFT_SEEKBAR_PROGRESS_HIGH, progressLow);
    }

    public static int getSeekBarHigh() {
        return DoKitSPUtil.getInt(SharedPrefsKey.ROUTE_DRIFT_SEEKBAR_PROGRESS_HIGH, 100);
    }


    public static void putLostLocSeekBarLow(int progressLow) {
        DoKitSPUtil.putInt(SharedPrefsKey.ROUTE_DRIFT_LOST_LOC_SEEKBAR_PROGRESS_LOW, progressLow);
    }

    public static int getLostLocSeekBarLow() {
        return DoKitSPUtil.getInt(SharedPrefsKey.ROUTE_DRIFT_LOST_LOC_SEEKBAR_PROGRESS_LOW, 0);
    }

    public static void putLostLocSeekBarHigh(int progressLow) {
        DoKitSPUtil.putInt(SharedPrefsKey.ROUTE_DRIFT_LOST_LOC_SEEKBAR_PROGRESS_HIGH, progressLow);
    }

    public static int getLostLocSeekBarHigh() {
        return DoKitSPUtil.getInt(SharedPrefsKey.ROUTE_DRIFT_LOST_LOC_SEEKBAR_PROGRESS_HIGH, 100);
    }

    public static LatLng getMockLocation() {
        return (LatLng) DoKitCacheUtils.readObject(CachesKey.MOCK_LOCATION);
    }

    public static void saveMockLocation(LatLng latLng) {
        DoKitCacheUtils.saveObject(CachesKey.MOCK_LOCATION, latLng);
    }
}
