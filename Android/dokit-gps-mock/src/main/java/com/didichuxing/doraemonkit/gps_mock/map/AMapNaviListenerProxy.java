package com.didichuxing.doraemonkit.gps_mock.map;

import com.amap.api.navi.AMapNavi;
import com.amap.api.navi.AMapNaviListener;
import com.amap.api.navi.model.AMapCalcRouteResult;
import com.amap.api.navi.model.AMapLaneInfo;
import com.amap.api.navi.model.AMapModelCross;
import com.amap.api.navi.model.AMapNaviCameraInfo;
import com.amap.api.navi.model.AMapNaviCross;
import com.amap.api.navi.model.AMapNaviLink;
import com.amap.api.navi.model.AMapNaviLocation;
import com.amap.api.navi.model.AMapNaviPath;
import com.amap.api.navi.model.AMapNaviRouteNotifyData;
import com.amap.api.navi.model.AMapNaviStep;
import com.amap.api.navi.model.AMapNaviTrafficFacilityInfo;
import com.amap.api.navi.model.AMapServiceAreaInfo;
import com.amap.api.navi.model.AimLessModeCongestionInfo;
import com.amap.api.navi.model.AimLessModeStat;
import com.amap.api.navi.model.NaviInfo;
import com.amap.api.navi.model.NaviLatLng;
import com.baidu.mapapi.search.core.RouteNode;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.didichuxing.doraemonkit.DoKit;
import com.didichuxing.doraemonkit.config.GpsMockConfig;
import com.didichuxing.doraemonkit.gps_mock.common.BdMapRouteData;
import com.didichuxing.doraemonkit.gps_mock.gpsmock.GpsMockManager;
import com.didichuxing.doraemonkit.gps_mock.gpsmock.GpsMockProxyManager;
import com.didichuxing.doraemonkit.util.CoordinateUtils;
import com.didichuxing.doraemonkit.util.LogHelper;

import java.util.ArrayList;
import java.util.Arrays;
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
        if (GpsMockManager.getInstance().isMocking()) {
            double[] res = CoordinateUtils.bd09ToGcj02(GpsMockManager.getInstance().getLongitude(), GpsMockManager.getInstance().getLatitude());
            aMapNaviLocation.setCoord(new NaviLatLng(res[1], res[0]));
            aMapNaviLocation.setSpeed(GpsMockConfig.getRouteMockSpeed());
        }
        LogHelper.d(TAG, "===amap===onLocationChange" + aMapNaviLocation.getCoord().toString());
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
        LogHelper.d(TAG, "====onNaviInfoUpdate====" + naviInfo.getPathRetainDistance());
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
            // 重新做了路径规划,则尝试打断当前模拟.
            GpsMockManager.getInstance().interruptRouteMockThread();
            int[] routIds = aMapCalcRouteResult.getRouteid();
            // 获取路线数据对象
            HashMap<Integer, AMapNaviPath> naviPaths = AMapNavi.getInstance(DoKit.INSTANCE.getAPPLICATION()).getNaviPaths();
            LogHelper.d(TAG, "===amap===onCalculateRouteSuccess $naviPaths " + naviPaths.size() + " routeId " + Arrays.toString(routIds));

            CoordinateConverter converter  = new CoordinateConverter().from(CoordinateConverter.CoordType.COMMON);
            // 将所有点转换成百度坐标点
            List<com.baidu.mapapi.model.LatLng> bdPoints = new ArrayList<>();
            AMapNaviPath naviPath = naviPaths.get(routIds[0]);
            List<AMapNaviStep> steps = naviPath.getSteps();
            int linkSize = 0;
            int stepSize = 0;
            for (AMapNaviStep step : steps) {
                stepSize += step.getCoords().size();
                List<AMapNaviLink> links = step.getLinks();
                boolean notNavi = false;
                if (links != null && links.size() > 0){
                    for (AMapNaviLink link : links){
                        if (link == null) continue;
                        int linkType = link.getLinkType(); // 获取道路类型 0-普通道路 1-航道 2-隧道 3-桥梁 4-高架桥 注意：该接口仅驾车模式有效
                        int roadClass = link.getRoadClass(); //获取该Link道路等级 * 0 高速公路 * 1 国道 * 2 省道 * 3 县道 * 4 乡公路 * 5 县乡村内部道路 * 6 主要大街、城市快速道 * 7 主要道路 * 8 次要道路 * 9 普通道路 * 10 非导航道路
                        List<NaviLatLng> coords = link.getCoords();
                        if (coords == null) continue;
                        linkSize += coords.size();
                        // 过滤非开车导航坐标点(下车后)
                        if (roadClass == 10){
                            notNavi = true;
                            break;
                        }
                    }
                }

                if (notNavi) continue;
                for (NaviLatLng latLng : step.getCoords()){
                    com.baidu.mapapi.model.LatLng sourceLatLng = new com.baidu.mapapi.model.LatLng(latLng.getLatitude(), latLng.getLongitude());
                    //转换坐标
                    converter.coord(sourceLatLng);
                    com.baidu.mapapi.model.LatLng desLatLng = converter.convert();
                    bdPoints.add(desLatLng);
                }
            }

            LogHelper.d(TAG, "===amap===total points==>" + naviPath.getCoordList().size() + " " + naviPath.getCoordList().get(naviPath.getCoordList().size() - 1)
                + " \n" + bdPoints.size() + " " + bdPoints.get(bdPoints.size() - 1)
                + "\nlinkSize=" + linkSize + " stepSize=" + stepSize);
            BdMapRouteData bdMapRouteData = new BdMapRouteData();
            bdMapRouteData.setAllPoints(bdPoints);
            bdMapRouteData.setTotalDistance(naviPath.getAllLength());

            RouteNode startNode = new RouteNode();
            com.baidu.mapapi.model.LatLng sourceStartLatLng = new com.baidu.mapapi.model.LatLng(naviPath.getStartPoint().getLatitude(), naviPath.getStartPoint().getLongitude());
            com.baidu.mapapi.model.LatLng desStartLatLng = converter.coord(sourceStartLatLng).convert();
            startNode.setLocation(desStartLatLng);
            RouteNode terminalNode = new RouteNode();
            com.baidu.mapapi.model.LatLng sourceTerminalLatLng = new com.baidu.mapapi.model.LatLng(naviPath.getEndPoint().getLatitude(), naviPath.getEndPoint().getLongitude());
            com.baidu.mapapi.model.LatLng desTerminalLatLng = converter.coord(sourceTerminalLatLng).convert();
            terminalNode.setLocation(desTerminalLatLng);
            bdMapRouteData.setStartNode(startNode);
            bdMapRouteData.setTerminalNode(terminalNode);
            bdMapRouteData.setRouteDataFromBiz(true);

            GpsMockManager.getInstance().setBdMockDrivingRouteLine(bdMapRouteData);

            if (aMapNaviListener != null) {
                aMapNaviListener.onCalculateRouteSuccess(aMapCalcRouteResult);
            }
        } catch (Exception e) {
            LogHelper.e(TAG, "===amap===onCalculateRouteSuccess error " + e.getMessage());
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
