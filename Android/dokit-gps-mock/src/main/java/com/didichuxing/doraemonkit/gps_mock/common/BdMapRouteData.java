package com.didichuxing.doraemonkit.gps_mock.common;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.RouteNode;

import java.util.ArrayList;
import java.util.List;

public class BdMapRouteData {
    private final List<LatLng> mAllPoints = new ArrayList<>();
    // 单位米
    private int mTotalDistance;
    private RouteNode mStartNode;
    private RouteNode mTerminalNode;

    // 路线数据是否来自业务方的路径规划
    private boolean mRouteDataFromBiz = true;

    List<com.baidu.mapapi.model.LatLng> mRandomDriftPoints = new ArrayList<>();
    List<com.baidu.mapapi.model.LatLng> mRouteDriftPoints = new ArrayList<>();
    List<com.baidu.mapapi.model.LatLng> mRouteLostLocPoints = new ArrayList<>();

    private int mRandomDriftDistance;
    private int mRouteDriftDistance;

    public com.baidu.mapapi.model.LatLng mOriginRouteStartLostPoint;
    public com.baidu.mapapi.model.LatLng mOriginRouteEndLostPoint;
    public com.baidu.mapapi.model.LatLng mRandomDriftStartLostPoint;
    public com.baidu.mapapi.model.LatLng mRandomDriftEndLostPoint;
    public com.baidu.mapapi.model.LatLng mRouteDriftStartLostPoint;
    public com.baidu.mapapi.model.LatLng mRouteDriftEndLostPoint;

    public List<LatLng> getAllPoints() {
        return mAllPoints;
    }

    public void setAllPoints(List<LatLng> allPoints) {
        mAllPoints.clear();
        mAllPoints.addAll(allPoints);
    }

    public int getTotalDistance() {
        return mTotalDistance;
    }

    public void setTotalDistance(int totalDistance) {
        mTotalDistance = totalDistance;
    }

    public RouteNode getStartNode() {
        return mStartNode;
    }

    public void setStartNode(RouteNode startNode) {
        mStartNode = startNode;
    }

    public RouteNode getTerminalNode() {
        return mTerminalNode;
    }

    public void setTerminalNode(RouteNode terminalNode) {
        mTerminalNode = terminalNode;
    }

    public List<LatLng> getRandomDriftPoints() {
        return mRandomDriftPoints;
    }

    public void setRandomDriftPoints(List<LatLng> randomDriftPoints) {
        mRandomDriftPoints.clear();
        mRandomDriftPoints.addAll(randomDriftPoints);
    }

    public List<LatLng> getRouteDriftPoints() {
        return mRouteDriftPoints;
    }

    public void setRouteDriftPoints(List<LatLng> routeDriftPoints) {
        mRouteDriftPoints.clear();
        mRouteDriftPoints.addAll(routeDriftPoints);
    }


    public List<LatLng> getOriginRouteLostLocPoints() {
        return mRouteLostLocPoints;
    }

    public void setOriginRouteLostLocPoints(List<LatLng> routeLostLocPoints) {
        mRouteLostLocPoints.clear();
        mRouteLostLocPoints.addAll(routeLostLocPoints);
    }

    public boolean isRouteDataFromBiz() {
        return mRouteDataFromBiz;
    }

    public void setRouteDataFromBiz(boolean routeDataFromBiz) {
        mRouteDataFromBiz = routeDataFromBiz;
    }

    public int getRandomDriftDistance() {
        return mRandomDriftDistance;
    }

    public void setRandomDriftDistance(int randomDriftDistance) {
        mRandomDriftDistance = randomDriftDistance;
    }

    public int getRouteDriftDistance() {
        return mRouteDriftDistance;
    }

    public void setRouteDriftDistance(int routeDriftDistance) {
        mRouteDriftDistance = routeDriftDistance;
    }
}
