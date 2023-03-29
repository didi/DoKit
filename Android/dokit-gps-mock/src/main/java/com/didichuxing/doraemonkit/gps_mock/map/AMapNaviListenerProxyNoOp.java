package com.didichuxing.doraemonkit.gps_mock.map;

import androidx.annotation.NonNull;

import com.amap.api.navi.AMapNaviListener;
import com.amap.api.navi.MyNaviListener;
import com.amap.api.navi.model.AMapCalcRouteResult;
import com.amap.api.navi.model.AMapLaneInfo;
import com.amap.api.navi.model.AMapModelCross;
import com.amap.api.navi.model.AMapNaviCameraInfo;
import com.amap.api.navi.model.AMapNaviCross;
import com.amap.api.navi.model.AMapNaviLocation;
import com.amap.api.navi.model.AMapNaviRouteNotifyData;
import com.amap.api.navi.model.AMapNaviTrafficFacilityInfo;
import com.amap.api.navi.model.AMapServiceAreaInfo;
import com.amap.api.navi.model.AimLessModeCongestionInfo;
import com.amap.api.navi.model.AimLessModeStat;
import com.amap.api.navi.model.InnerNaviInfo;
import com.amap.api.navi.model.NaviCongestionInfo;
import com.amap.api.navi.model.NaviInfo;
import com.amap.api.navi.model.NaviPath;

/**
 * didi Create on 2023/3/28 .
 * <p>
 * Copyright (c) 2023/3/28 by didiglobal.com.
 *
 * @author <a href="realonlyone@126.com">zhangjun</a>
 * @version 1.0
 * @Date 2023/3/28 8:06 下午
 * @Description 用一句话说明文件功能
 */

public class AMapNaviListenerProxyNoOp implements MyNaviListener {


    @NonNull
    private AMapNaviListener naviListener;

    public AMapNaviListenerProxyNoOp(@NonNull AMapNaviListener naviListener) {
        this.naviListener = naviListener;
    }

    public AMapNaviListener getNaviListener() {
        return naviListener;
    }


    @Override
    public void onInitNaviFailure() {
        naviListener.onInitNaviFailure();
    }

    @Override
    public void onInitNaviSuccess() {
        naviListener.onInitNaviSuccess();
    }

    @Override
    public void onStartNavi(int i) {
        naviListener.onStartNavi(i);
    }

    @Override
    public void onTrafficStatusUpdate() {
        naviListener.onTrafficStatusUpdate();
    }

    @Override
    public void onLocationChange(AMapNaviLocation aMapNaviLocation) {
        naviListener.onLocationChange(aMapNaviLocation);
    }

    @Override
    public void onGetNavigationText(int i, String s) {
        naviListener.onGetNavigationText(i, s);
    }

    @Override
    public void onGetNavigationText(String s) {
        naviListener.onGetNavigationText(s);
    }

    @Override
    public void onEndEmulatorNavi() {
        naviListener.onEndEmulatorNavi();
    }

    @Override
    public void onArriveDestination() {
        naviListener.onArriveDestination();
    }

    @Override
    public void onCalculateRouteFailure(int i) {
        naviListener.onCalculateRouteFailure(i);
    }

    @Override
    public void onReCalculateRouteForYaw() {
        naviListener.onReCalculateRouteForYaw();
    }

    @Override
    public void onReCalculateRouteForTrafficJam() {
        naviListener.onReCalculateRouteForTrafficJam();
    }

    @Override
    public void onArrivedWayPoint(int i) {
        naviListener.onArrivedWayPoint(i);
    }

    @Override
    public void onGpsOpenStatus(boolean b) {
        naviListener.onGpsOpenStatus(b);
    }

    @Override
    public void onNaviInfoUpdate(NaviInfo naviInfo) {
        naviListener.onNaviInfoUpdate(naviInfo);
    }

    @Override
    public void updateCameraInfo(AMapNaviCameraInfo[] aMapNaviCameraInfos) {
        naviListener.updateCameraInfo(aMapNaviCameraInfos);
    }

    @Override
    public void updateIntervalCameraInfo(AMapNaviCameraInfo aMapNaviCameraInfo, AMapNaviCameraInfo aMapNaviCameraInfo1, int i) {
        naviListener.updateIntervalCameraInfo(aMapNaviCameraInfo, aMapNaviCameraInfo1, i);
    }

    @Override
    public void onServiceAreaUpdate(AMapServiceAreaInfo[] aMapServiceAreaInfos) {
        naviListener.onServiceAreaUpdate(aMapServiceAreaInfos);
    }

    @Override
    public void showCross(AMapNaviCross aMapNaviCross) {
        naviListener.showCross(aMapNaviCross);
    }

    @Override
    public void hideCross() {
        naviListener.hideCross();
    }

    @Override
    public void showModeCross(AMapModelCross aMapModelCross) {
        naviListener.showModeCross(aMapModelCross);
    }

    @Override
    public void hideModeCross() {
        naviListener.hideModeCross();
    }

    @Override
    public void showLaneInfo(AMapLaneInfo[] aMapLaneInfos, byte[] bytes, byte[] bytes1) {
        naviListener.showLaneInfo(aMapLaneInfos, bytes, bytes1);
    }

    @Override
    public void showLaneInfo(AMapLaneInfo aMapLaneInfo) {
        naviListener.showLaneInfo(aMapLaneInfo);
    }

    @Override
    public void hideLaneInfo() {
        naviListener.hideLaneInfo();
    }

    @Override
    public void onCalculateRouteSuccess(int[] ints) {
        naviListener.onCalculateRouteSuccess(ints);
    }

    @Override
    public void notifyParallelRoad(int i) {
        naviListener.notifyParallelRoad(i);
    }

    @Override
    public void OnUpdateTrafficFacility(AMapNaviTrafficFacilityInfo[] aMapNaviTrafficFacilityInfos) {
        naviListener.OnUpdateTrafficFacility(aMapNaviTrafficFacilityInfos);
    }

    @Override
    public void OnUpdateTrafficFacility(AMapNaviTrafficFacilityInfo aMapNaviTrafficFacilityInfo) {
        naviListener.OnUpdateTrafficFacility(aMapNaviTrafficFacilityInfo);
    }

    @Override
    public void updateAimlessModeStatistics(AimLessModeStat aimLessModeStat) {
        naviListener.updateAimlessModeStatistics(aimLessModeStat);
    }

    @Override
    public void updateAimlessModeCongestionInfo(AimLessModeCongestionInfo aimLessModeCongestionInfo) {
        naviListener.updateAimlessModeCongestionInfo(aimLessModeCongestionInfo);
    }

    @Override
    public void onPlayRing(int i) {
        naviListener.onPlayRing(i);
    }

    @Override
    public void onCalculateRouteSuccess(AMapCalcRouteResult aMapCalcRouteResult) {
        naviListener.onCalculateRouteSuccess(aMapCalcRouteResult);
    }

    @Override
    public void onCalculateRouteFailure(AMapCalcRouteResult aMapCalcRouteResult) {
        naviListener.onCalculateRouteFailure(aMapCalcRouteResult);
    }

    @Override
    public void onNaviRouteNotify(AMapNaviRouteNotifyData aMapNaviRouteNotifyData) {
        naviListener.onNaviRouteNotify(aMapNaviRouteNotifyData);
    }

    @Override
    public void onGpsSignalWeak(boolean b) {
        naviListener.onGpsSignalWeak(b);
    }

    //----------MyNaviListener----------


    @Override
    public void onInnerNaviInfoUpdate(InnerNaviInfo innerNaviInfo) {
        if (naviListener instanceof MyNaviListener) {
            ((MyNaviListener) naviListener).onInnerNaviInfoUpdate(innerNaviInfo);
        }
    }

    @Override
    public void onInnerNaviInfoUpdate(InnerNaviInfo[] innerNaviInfos) {
        if (naviListener instanceof MyNaviListener) {
            ((MyNaviListener) naviListener).onInnerNaviInfoUpdate(innerNaviInfos);
        }
    }

    @Override
    public void onUpdateTmcStatus(NaviCongestionInfo naviCongestionInfo) {
        if (naviListener instanceof MyNaviListener) {
            ((MyNaviListener) naviListener).onUpdateTmcStatus(naviCongestionInfo);
        }
    }

    @Override
    public void onStopNavi() {
        if (naviListener instanceof MyNaviListener) {
            ((MyNaviListener) naviListener).onStopNavi();
        }
    }

    @Override
    public void onSelectRouteId(int i) {
        if (naviListener instanceof MyNaviListener) {
            ((MyNaviListener) naviListener).onSelectRouteId(i);
        }
    }

    @Override
    public void updateBackupPath(NaviPath[] naviPaths) {
        if (naviListener instanceof MyNaviListener) {
            ((MyNaviListener) naviListener).updateBackupPath(naviPaths);
        }
    }

    @Override
    public void onSuggestChangePath(long l, long l1, int i, String s) {
        if (naviListener instanceof MyNaviListener) {
            ((MyNaviListener) naviListener).onSuggestChangePath(l, l1, i, s);
        }
    }

    @Override
    public void onUpdateNaviPath() {
        if (naviListener instanceof MyNaviListener) {
            ((MyNaviListener) naviListener).onUpdateNaviPath();
        }
    }

    @Override
    public void onUpdateGpsSignalStrength(int i) {
        if (naviListener instanceof MyNaviListener) {
            ((MyNaviListener) naviListener).onUpdateGpsSignalStrength(i);
        }
    }
}
