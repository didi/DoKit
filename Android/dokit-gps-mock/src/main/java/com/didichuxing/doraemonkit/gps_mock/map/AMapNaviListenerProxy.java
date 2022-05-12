package com.didichuxing.doraemonkit.gps_mock.map;

import android.util.Log;

import com.amap.api.navi.AMapNavi;
import com.amap.api.navi.AMapNaviListener;
import com.amap.api.navi.model.AMapCalcRouteResult;
import com.amap.api.navi.model.AMapLaneInfo;
import com.amap.api.navi.model.AMapModelCross;
import com.amap.api.navi.model.AMapNaviCameraInfo;
import com.amap.api.navi.model.AMapNaviCross;
import com.amap.api.navi.model.AMapNaviLocation;
import com.amap.api.navi.model.AMapNaviPath;
import com.amap.api.navi.model.AMapNaviRouteNotifyData;
import com.amap.api.navi.model.AMapNaviTrafficFacilityInfo;
import com.amap.api.navi.model.AMapServiceAreaInfo;
import com.amap.api.navi.model.AimLessModeCongestionInfo;
import com.amap.api.navi.model.AimLessModeStat;
import com.amap.api.navi.model.NaviInfo;
import com.amap.api.navi.model.NaviLatLng;
import com.didichuxing.doraemonkit.DoKit;
import com.didichuxing.doraemonkit.gps_mock.gpsmock.GpsMockManager;
import com.didichuxing.doraemonkit.gps_mock.gpsmock.GpsMockProxyManager;

import java.util.HashMap;
import java.util.List;

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2019-12-15-16:18
 * 描    述：高德AMapLocationListenerProxy 通过ASM代码动态插入 高德不会跟随系统hook 腾讯和百度会跟随系统的hook
 * 修订历史：
 * ================================================
 */
public class AMapNaviListenerProxy implements AMapNaviListener {
    private static final String TAG = "AMapNaviListenerProxy";
    public AMapNaviListener aMapNaviListener;

    public AMapNaviListenerProxy(AMapNaviListener aMapNaviListener) {
        this.aMapNaviListener = aMapNaviListener;
        GpsMockProxyManager.INSTANCE.addAMapNaviListenerProxy(this);
    }


    @Override
    public void onInitNaviFailure() {
        if (aMapNaviListener != null) {
            aMapNaviListener.onInitNaviFailure();
        }

    }

    @Override
    public void onInitNaviSuccess() {
        if (aMapNaviListener != null) {
            aMapNaviListener.onInitNaviSuccess();
        }
    }

    @Override
    public void onStartNavi(int i) {
        if (aMapNaviListener != null) {
            aMapNaviListener.onStartNavi(i);
        }
    }

    @Override
    public void onTrafficStatusUpdate() {
        if (aMapNaviListener != null) {
            aMapNaviListener.onTrafficStatusUpdate();
        }
    }

    @Override
    public void onLocationChange(AMapNaviLocation aMapNaviLocation) {
        if (GpsMockManager.mockAMapNavLocation()) {
            aMapNaviLocation.setCoord(new NaviLatLng(GpsMockManager.getInstance().getLatitude(), GpsMockManager.getInstance().getLongitude()));
        }
//        LogHelper.i(TAG, "====aMapNaviLocation===" + aMapNaviLocation.getCoord().toString());
        if (aMapNaviListener != null) {
            aMapNaviListener.onLocationChange(aMapNaviLocation);
        }
    }

    @Override
    public void onGetNavigationText(int i, String s) {
        if (aMapNaviListener != null) {
            aMapNaviListener.onGetNavigationText(i, s);
        }
    }

    @Override
    public void onGetNavigationText(String s) {
        if (aMapNaviListener != null) {
            aMapNaviListener.onGetNavigationText(s);
        }
    }

    @Override
    public void onEndEmulatorNavi() {
        if (aMapNaviListener != null) {
            aMapNaviListener.onEndEmulatorNavi();
        }
    }

    @Override
    public void onArriveDestination() {
        if (aMapNaviListener != null) {
            aMapNaviListener.onArriveDestination();
        }
    }

    @Override
    public void onCalculateRouteFailure(int i) {
        if (aMapNaviListener != null) {
            aMapNaviListener.onCalculateRouteFailure(i);
        }
    }

    @Override
    public void onReCalculateRouteForYaw() {
        if (aMapNaviListener != null) {
            aMapNaviListener.onReCalculateRouteForYaw();
        }
    }

    @Override
    public void onReCalculateRouteForTrafficJam() {
        if (aMapNaviListener != null) {
            aMapNaviListener.onReCalculateRouteForTrafficJam();
        }
    }

    @Override
    public void onArrivedWayPoint(int i) {
        if (aMapNaviListener != null) {
            aMapNaviListener.onArrivedWayPoint(i);
        }
    }

    @Override
    public void onGpsOpenStatus(boolean b) {
        if (aMapNaviListener != null) {
            aMapNaviListener.onGpsOpenStatus(b);
        }
    }

    @Override
    public void onNaviInfoUpdate(NaviInfo naviInfo) {
//        LogHelper.i(TAG, "====onNaviInfoUpdate====" + naviInfo.getPathRetainDistance());
        if (aMapNaviListener != null) {
            aMapNaviListener.onNaviInfoUpdate(naviInfo);
        }
    }

    @Override
    public void updateCameraInfo(AMapNaviCameraInfo[] aMapNaviCameraInfos) {
        if (aMapNaviListener != null) {
            aMapNaviListener.updateCameraInfo(aMapNaviCameraInfos);
        }
    }

    @Override
    public void updateIntervalCameraInfo(AMapNaviCameraInfo aMapNaviCameraInfo, AMapNaviCameraInfo aMapNaviCameraInfo1, int i) {
        if (aMapNaviListener != null) {
            aMapNaviListener.updateIntervalCameraInfo(aMapNaviCameraInfo, aMapNaviCameraInfo1, i);
        }
    }

    @Override
    public void onServiceAreaUpdate(AMapServiceAreaInfo[] aMapServiceAreaInfos) {
        if (aMapNaviListener != null) {
            aMapNaviListener.onServiceAreaUpdate(aMapServiceAreaInfos);
        }
    }

    @Override
    public void showCross(AMapNaviCross aMapNaviCross) {
        if (aMapNaviListener != null) {
            aMapNaviListener.showCross(aMapNaviCross);
        }
    }

    @Override
    public void hideCross() {
        if (aMapNaviListener != null) {
            aMapNaviListener.hideCross();
        }
    }

    @Override
    public void showModeCross(AMapModelCross aMapModelCross) {
        if (aMapNaviListener != null) {
            aMapNaviListener.showModeCross(aMapModelCross);
        }
    }

    @Override
    public void hideModeCross() {
        if (aMapNaviListener != null) {
            aMapNaviListener.hideModeCross();
        }
    }

    @Override
    public void showLaneInfo(AMapLaneInfo[] aMapLaneInfos, byte[] bytes, byte[] bytes1) {
        if (aMapNaviListener != null) {
            aMapNaviListener.showLaneInfo(aMapLaneInfos, bytes, bytes1);
        }
    }

    @Override
    public void showLaneInfo(AMapLaneInfo aMapLaneInfo) {
        if (aMapNaviListener != null) {
            aMapNaviListener.showLaneInfo(aMapLaneInfo);
        }
    }

    @Override
    public void hideLaneInfo() {
        if (aMapNaviListener != null) {
            aMapNaviListener.hideLaneInfo();
        }
    }

    @Override
    public void onCalculateRouteSuccess(int[] ints) {
        if (aMapNaviListener != null) {
            aMapNaviListener.onCalculateRouteSuccess(ints);
        }
    }

    @Override
    public void notifyParallelRoad(int i) {
        if (aMapNaviListener != null) {
            aMapNaviListener.notifyParallelRoad(i);
        }
    }

    @Override
    public void OnUpdateTrafficFacility(AMapNaviTrafficFacilityInfo[] aMapNaviTrafficFacilityInfos) {
        if (aMapNaviListener != null) {
            aMapNaviListener.OnUpdateTrafficFacility(aMapNaviTrafficFacilityInfos);
        }
    }

    @Override
    public void OnUpdateTrafficFacility(AMapNaviTrafficFacilityInfo aMapNaviTrafficFacilityInfo) {
        if (aMapNaviListener != null) {
            aMapNaviListener.OnUpdateTrafficFacility(aMapNaviTrafficFacilityInfo);
        }
    }

    @Override
    public void updateAimlessModeStatistics(AimLessModeStat aimLessModeStat) {
        if (aMapNaviListener != null) {
            aMapNaviListener.updateAimlessModeStatistics(aimLessModeStat);
        }
    }

    @Override
    public void updateAimlessModeCongestionInfo(AimLessModeCongestionInfo aimLessModeCongestionInfo) {
        if (aMapNaviListener != null) {
            aMapNaviListener.updateAimlessModeCongestionInfo(aimLessModeCongestionInfo);
        }
    }

    @Override
    public void onPlayRing(int i) {
        if (aMapNaviListener != null) {
            aMapNaviListener.onPlayRing(i);
        }
    }

    @Override
    public void onCalculateRouteSuccess(AMapCalcRouteResult aMapCalcRouteResult) {
        try {
            // 获取路线数据对象
            HashMap<Integer, AMapNaviPath> naviPaths = AMapNavi.getInstance(DoKit.INSTANCE.getAPPLICATION()).getNaviPaths();
            Log.i(TAG, "高德导航===onCalculateRouteSuccess $naviPaths " + naviPaths + " " + aMapCalcRouteResult.toString());
            // 绘制显示路径
            List<NaviLatLng> coords = naviPaths.get(12).getSteps().get(0).getCoords();
            for (NaviLatLng latLng : coords) {
                Log.i(TAG, "高德导航===onCalculateRouteSuccess $naviPaths " + latLng.toString());
            }

            if (aMapNaviListener != null) {
                aMapNaviListener.onCalculateRouteSuccess(aMapCalcRouteResult);
            }
        }catch (Exception e){
            Log.e(TAG, e.getMessage());
        }
    }

    @Override
    public void onCalculateRouteFailure(AMapCalcRouteResult aMapCalcRouteResult) {
        if (aMapNaviListener != null) {
            aMapNaviListener.onCalculateRouteFailure(aMapCalcRouteResult);
        }
    }

    @Override
    public void onNaviRouteNotify(AMapNaviRouteNotifyData aMapNaviRouteNotifyData) {
        if (aMapNaviListener != null) {
            aMapNaviListener.onNaviRouteNotify(aMapNaviRouteNotifyData);
        }
    }

    @Override
    public void onGpsSignalWeak(boolean b) {
        if (aMapNaviListener != null) {
            aMapNaviListener.onGpsSignalWeak(b);
        }
    }
}
