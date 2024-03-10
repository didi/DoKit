package com.didichuxing.doraemonkit.gps_mock.map;

import com.amap.api.navi.AMapNavi;
import com.amap.api.navi.model.AMapCalcRouteResult;
import com.amap.api.navi.model.AMapNaviLink;
import com.amap.api.navi.model.AMapNaviLocation;
import com.amap.api.navi.model.AMapNaviPath;
import com.amap.api.navi.model.AMapNaviStep;
import com.amap.api.navi.model.NaviLatLng;
import com.baidu.mapapi.search.core.RouteNode;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.didichuxing.doraemonkit.DoKit;
import com.didichuxing.doraemonkit.config.GpsMockConfig;
import com.didichuxing.doraemonkit.gps_mock.common.BdMapRouteData;
import com.didichuxing.doraemonkit.gps_mock.gpsmock.GpsMockManager;
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
public class AMapNaviListenerProxyDelegate {
    private static final String TAG = "AMapNaviListenerProxy";

    public AMapNaviListenerProxyDelegate() {

    }

    public void onLocationChange(AMapNaviLocation aMapNaviLocation) {
        if (GpsMockManager.getInstance().isMocking()) {
            double[] res = CoordinateUtils.bd09ToGcj02(GpsMockManager.getInstance().getLongitude(), GpsMockManager.getInstance().getLatitude());
            aMapNaviLocation.setCoord(new NaviLatLng(res[1], res[0]));
            aMapNaviLocation.setSpeed(GpsMockConfig.getRouteMockSpeed());
        }
        LogHelper.d(TAG, "===amap===onLocationChange" + aMapNaviLocation.getCoord().toString());
    }


    public void onCalculateRouteSuccess(AMapCalcRouteResult aMapCalcRouteResult) {
        if (GpsMockManager.getInstance().isMocking()) {
            try {
                // 重新做了路径规划,则尝试打断当前模拟.
                GpsMockManager.getInstance().interruptRouteMockThread();
                int[] routIds = aMapCalcRouteResult.getRouteid();
                // 获取路线数据对象
                HashMap<Integer, AMapNaviPath> naviPaths = AMapNavi.getInstance(DoKit.INSTANCE.getAPPLICATION()).getNaviPaths();
                LogHelper.d(TAG, "===amap===onCalculateRouteSuccess $naviPaths " + naviPaths.size() + " routeId " + Arrays.toString(routIds));

                CoordinateConverter converter = new CoordinateConverter().from(CoordinateConverter.CoordType.COMMON);
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
                    if (links != null && links.size() > 0) {
                        for (AMapNaviLink link : links) {
                            if (link == null) continue;
                            int linkType = link.getLinkType(); // 获取道路类型 0-普通道路 1-航道 2-隧道 3-桥梁 4-高架桥 注意：该接口仅驾车模式有效
                            int roadClass = link.getRoadClass(); //获取该Link道路等级 * 0 高速公路 * 1 国道 * 2 省道 * 3 县道 * 4 乡公路 * 5 县乡村内部道路 * 6 主要大街、城市快速道 * 7 主要道路 * 8 次要道路 * 9 普通道路 * 10 非导航道路
                            List<NaviLatLng> coords = link.getCoords();
                            if (coords == null) continue;
                            linkSize += coords.size();
                            // 过滤非开车导航坐标点(下车后)
                            if (roadClass == 10) {
                                notNavi = true;
                                break;
                            }
                        }
                    }

                    if (notNavi) continue;
                    for (NaviLatLng latLng : step.getCoords()) {
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

            } catch (Exception e) {
                LogHelper.e(TAG, "===amap===onCalculateRouteSuccess error " + e.getMessage());
            }
        }
    }

}
