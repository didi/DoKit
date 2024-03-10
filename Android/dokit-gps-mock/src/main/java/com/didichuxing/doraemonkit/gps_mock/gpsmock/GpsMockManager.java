package com.didichuxing.doraemonkit.gps_mock.gpsmock;

import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.didichuxing.doraemonkit.config.GpsMockConfig;
import com.didichuxing.doraemonkit.gps_mock.common.BdMapRouteData;
import com.didichuxing.doraemonkit.gps_mock.common.Utils;
import com.didichuxing.doraemonkit.model.LatLng;
import com.didichuxing.doraemonkit.util.LogHelper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Created by wanglikun on 2018/12/18.
 */

public class GpsMockManager {
    private static final String TAG = "GpsMockManager";

    private static double mLatitude = -1;
    private static double mLongitude = -1;

    private static boolean isMocking;
    private RouteMockThread mRouteMockThread;

    // 待mock的路径(百度驾车路线)
    private BdMapRouteData mBdMockDrivingRouteLine;

    // 坐标点移动间隔时间
    private long mIntervalTime;

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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            location.setElapsedRealtimeNanos(currentTimeMillis);
        }
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
        return ServiceHookManager.INSTANCE.isHookSuccess();
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

    public boolean isMockingRoute() {
        return mRouteMockThread != null && mRouteMockThread.isMocking();
    }

    public BdMapRouteData getBdMockDrivingRouteLine() {
        return mBdMockDrivingRouteLine;
    }

    public void setBdMockDrivingRouteLine(BdMapRouteData bdMockDrivingRouteLine) {
        mBdMockDrivingRouteLine = bdMockDrivingRouteLine;
    }

    public void performMock(LatLng latLng) {
        if (latLng == null) return;
        LogHelper.d(TAG, " performMock " + latLng.latitude + " " + latLng.longitude);
        GpsMockManager.getInstance().mockLocationWithNotify(latLng.latitude, latLng.longitude);
        GpsMockConfig.saveMockLocation(new LatLng(latLng.latitude, latLng.longitude));
    }

    public void startMockRouteLine(List<com.baidu.mapapi.model.LatLng> points, double speed, RouteMockThread.RouteMockStatusCallback statusCallback) {
        if (isMockingRoute()) return;

        if (isMockingRoute() && mRouteMockThread.isSuspend()) {
            suspendRouteMock(false);
            return;
        }

        if (points != null && points.size() > 0) {
            mIntervalTime = Math.round(((double) GpsMockManager.getInstance().getBdMockDrivingRouteLine().getTotalDistance()) * 3600000 / (speed * 1000 * points.size()));
            LogHelper.d(TAG, "mIntervalTime " + mIntervalTime);

            mRouteMockThread = new RouteMockThread();
            mRouteMockThread.setIntervalTime(mIntervalTime);
            mRouteMockThread.setPoints(points);
            mRouteMockThread.setRouteMockStatusCallback(statusCallback);
            mRouteMockThread.start();
        }
    }

    public void setStatusCallback(RouteMockThread.RouteMockStatusCallback statusCallback) {
        if (mRouteMockThread != null) {
            mRouteMockThread.setRouteMockStatusCallback(statusCallback);
        }
    }

    public void removeStatusCallback() {
        if (mRouteMockThread != null) {
            mRouteMockThread.clearRouteMockStatusCallback();
        }
    }

    /**
     * 停止模拟.
     */
    public void interruptRouteMockThread() {
        if (GpsMockManager.getInstance().isMockingRoute()) {
            mRouteMockThread.interrupt();
        }
    }

    /**
     * @param suspend true: 暂停模拟; false:继续模拟
     */
    public void suspendRouteMock(boolean suspend) {
        if (isMockingRoute()) {
            mRouteMockThread.notifyThread(suspend);
        }
    }

    public void calculateOriginRouteWithLocLost(double progressLow, double progressHigh) {
        BdMapRouteData bdMapRouteData = GpsMockManager.getInstance().getBdMockDrivingRouteLine();
        if (bdMapRouteData == null) return;
        List<com.baidu.mapapi.model.LatLng> originRoutePoints = bdMapRouteData.getAllPoints();
        int totalPointsSize = originRoutePoints.size();
        if (totalPointsSize < 2) return;

        int startIndex = Math.round((totalPointsSize / 100.0f) * (int) progressLow);
        int endIndex = Math.round((totalPointsSize / 100.0f) * (int) progressHigh);
        if (endIndex <= startIndex) return;
        int startLostIndex = startIndex > 0 ? (startIndex - 1) : 0;
        if (endIndex > totalPointsSize) {
            endIndex = totalPointsSize - 1;
        }

        startIndex = startIndex <= 0 ? 1 : startIndex;
        endIndex = endIndex == totalPointsSize ? (endIndex - 1) : endIndex;
        com.baidu.mapapi.model.LatLng originRouteStartLostPoint = originRoutePoints.get(startLostIndex);
        com.baidu.mapapi.model.LatLng originRouteEndLostPoint = originRoutePoints.get(endIndex);
        bdMapRouteData.mOriginRouteStartLostPoint = originRouteStartLostPoint;
        bdMapRouteData.mOriginRouteEndLostPoint = originRouteEndLostPoint;

        List<com.baidu.mapapi.model.LatLng> tempLostLocOriginRoutePoints = new ArrayList<>();
        tempLostLocOriginRoutePoints.addAll(originRoutePoints.subList(0, startIndex));
        tempLostLocOriginRoutePoints.addAll(originRoutePoints.subList(endIndex, totalPointsSize));
        bdMapRouteData.setOriginRouteLostLocPoints(tempLostLocOriginRoutePoints);
    }

    public void calculateDriftRouteWithLocLost(double progressLow, double progressHigh) {
        BdMapRouteData bdMapRouteData = GpsMockManager.getInstance().getBdMockDrivingRouteLine();
        if (bdMapRouteData == null) return;
        List<com.baidu.mapapi.model.LatLng> randomDriftPoints = bdMapRouteData.getRandomDriftPoints();
        List<com.baidu.mapapi.model.LatLng> routeDriftPoints = bdMapRouteData.getRouteDriftPoints();
        int totalPointsSize = randomDriftPoints.size();
        if (totalPointsSize < 2) return;
        int startIndex = Math.round((totalPointsSize / 100.0f) * (int) progressLow);
        int endIndex = Math.round((totalPointsSize / 100.0f) * (int) progressHigh);
        if (endIndex <= startIndex) return;
        int startLostIndex = startIndex > 0 ? (startIndex - 1) : 0;
        if (endIndex > totalPointsSize) {
            endIndex = totalPointsSize - 1;
        }

        startIndex = startIndex <= 0 ? 1 : startIndex;
        endIndex = endIndex == totalPointsSize ? (endIndex - 1) : endIndex;

        com.baidu.mapapi.model.LatLng randomDriftStartLostPoint = randomDriftPoints.get(startLostIndex);
        com.baidu.mapapi.model.LatLng randomDriftEndLostPoint = randomDriftPoints.get(endIndex);
        bdMapRouteData.mRandomDriftStartLostPoint = randomDriftStartLostPoint;
        bdMapRouteData.mRandomDriftEndLostPoint = randomDriftEndLostPoint;
        com.baidu.mapapi.model.LatLng routeDriftStartLostPoint = routeDriftPoints.get(startLostIndex);
        com.baidu.mapapi.model.LatLng routeDriftEndLostPoint = routeDriftPoints.get(endIndex);
        bdMapRouteData.mRouteDriftStartLostPoint = routeDriftStartLostPoint;
        bdMapRouteData.mRouteDriftEndLostPoint = routeDriftEndLostPoint;

        List<com.baidu.mapapi.model.LatLng> tempLostLocRandomDriftPoints = new ArrayList<>();
        tempLostLocRandomDriftPoints.addAll(randomDriftPoints.subList(0, startIndex));
        tempLostLocRandomDriftPoints.addAll(randomDriftPoints.subList(endIndex, totalPointsSize));
        bdMapRouteData.setRandomDriftPoints(tempLostLocRandomDriftPoints);
        List<com.baidu.mapapi.model.LatLng> tempLostLocRouteDriftPoints = new ArrayList<>();
        tempLostLocRouteDriftPoints.addAll(routeDriftPoints.subList(0, startIndex));
        tempLostLocRouteDriftPoints.addAll(routeDriftPoints.subList(endIndex, totalPointsSize));
        bdMapRouteData.setRouteDriftPoints(tempLostLocRouteDriftPoints);
    }

    public void calculateDriftRoute(double radius, double progressLow, double progressHigh) {
        Double orientLatDiffer = null;
        BdMapRouteData bdMapRouteData = GpsMockManager.getInstance().getBdMockDrivingRouteLine();
        if (bdMapRouteData != null && bdMapRouteData.getAllPoints().size() > 0) {
            List<com.baidu.mapapi.model.LatLng> allPoints = bdMapRouteData.getAllPoints();
            int totalPointsSize = allPoints.size();
            List<com.baidu.mapapi.model.LatLng> randomDriftPoints = new ArrayList<>();
            List<com.baidu.mapapi.model.LatLng> routeDriftPoints = new ArrayList<>();

            List<com.baidu.mapapi.model.LatLng> rangeDriftPoints;
            int startIndex = Math.round((totalPointsSize / 100.0f) * (int) progressLow);
            int endIndex = Math.round((totalPointsSize / 100.0f) * (int) progressHigh);
            if (endIndex >= totalPointsSize) {
                endIndex = totalPointsSize - 1;
            }
            rangeDriftPoints = bdMapRouteData.getAllPoints().subList(startIndex, endIndex);

            int rangeDriftPointsSize = rangeDriftPoints.size();
            // 从漂移段里再抽稀出10%的坐标点进行随机漂移
            int randomPointsSize = Math.round(rangeDriftPointsSize * 0.1f);
            if (randomPointsSize > rangeDriftPointsSize) {
                randomPointsSize = rangeDriftPointsSize;
            }

            // 获取随机坐标点的索引值
            HashSet<Integer> randomIndexSet = new HashSet<>();
            Utils.randomSet(0, rangeDriftPointsSize, randomPointsSize, randomIndexSet);
            LogHelper.d(TAG, "randomPointsSize:" + randomPointsSize + " rangeDriftPointsSize:" + rangeDriftPointsSize + " randomIndexSet " + randomIndexSet);

            for (int i = 0; i < rangeDriftPointsSize; i++) {
                com.baidu.mapapi.model.LatLng point = rangeDriftPoints.get(i);
                double[] rangeAround = Utils.getAround(point.latitude, point.longitude, radius);

                if (randomIndexSet.contains(i)) {
                    double[] randomLatLng = Utils.getRandomLatLng(point.latitude, point.longitude, radius, rangeAround);
                    com.baidu.mapapi.model.LatLng randomPoint = new com.baidu.mapapi.model.LatLng(randomLatLng[0], randomLatLng[1]);
                    randomDriftPoints.add(randomPoint);
                } else {
                    randomDriftPoints.add(point);
                }

                // 所有点偏移量都一样,即可实现路径整体平移效果.
                if (orientLatDiffer == null) {
                    orientLatDiffer = Utils.getOrientLatDiffer(rangeAround);
                }

                double[] orientLatLng = Utils.getOrientationLatLng(point.latitude, point.longitude, radius, rangeAround, orientLatDiffer);
                com.baidu.mapapi.model.LatLng orientPoint = new com.baidu.mapapi.model.LatLng(orientLatLng[0], orientLatLng[1]);
                routeDriftPoints.add(orientPoint);
            }

            List<com.baidu.mapapi.model.LatLng> start = allPoints.subList(0, startIndex);
            List<com.baidu.mapapi.model.LatLng> end = allPoints.subList(endIndex, totalPointsSize);
            randomDriftPoints.addAll(randomDriftPoints.size(), end);
            randomDriftPoints.addAll(0, start);
            int randomDriftDistance = (int) Math.round(Utils.getRouteDistance(randomDriftPoints));
            bdMapRouteData.setRandomDriftPoints(randomDriftPoints);
            bdMapRouteData.setRandomDriftDistance(randomDriftDistance);

            routeDriftPoints.addAll(routeDriftPoints.size(), end);
            routeDriftPoints.addAll(0, start);
            int routeDriftDistance = (int) Math.round(Utils.getRouteDistance(routeDriftPoints));
            bdMapRouteData.setRouteDriftPoints(routeDriftPoints);
            bdMapRouteData.setRouteDriftDistance(routeDriftDistance);
        }
    }
}
