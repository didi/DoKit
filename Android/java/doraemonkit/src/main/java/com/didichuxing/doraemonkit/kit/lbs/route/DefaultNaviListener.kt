package com.didichuxing.doraemonkit.kit.lbs.route

import com.amap.api.maps.AMap
import com.amap.api.navi.AMapNavi
import com.amap.api.navi.AMapNaviListener
import com.amap.api.navi.enums.NaviType
import com.amap.api.navi.model.*
import com.didichuxing.doraemonkit.util.LogHelper

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2/25/21-20:00
 * 描    述：
 * 修订历史：
 * ================================================
 */
class DefaultNaviListener(val mAMap: AMap, val mAMapNavi: AMapNavi) : AMapNaviListener {
//    private var mNaviRouteOverlay: NaviRouteOverlay? = null
//
//    init {
//        mNaviRouteOverlay = NaviRouteOverlay(mAMap, null)
//    }

    companion object {
        const val TAG = "DefaultNaviListener"
    }

    override fun onInitNaviFailure() {
    }

    override fun onInitNaviSuccess() {
    }

    override fun onStartNavi(p0: Int) {
    }

    override fun onTrafficStatusUpdate() {
    }

    override fun onLocationChange(p0: AMapNaviLocation?) {
    }

    override fun onGetNavigationText(p0: Int, p1: String?) {
    }

    override fun onGetNavigationText(p0: String?) {
    }

    override fun onEndEmulatorNavi() {
    }

    override fun onArriveDestination() {
    }

    override fun onCalculateRouteFailure(p0: Int) {
    }

    override fun onCalculateRouteFailure(p0: AMapCalcRouteResult?) {
    }

    override fun onReCalculateRouteForYaw() {
    }

    override fun onReCalculateRouteForTrafficJam() {
    }

    override fun onArrivedWayPoint(p0: Int) {
    }

    override fun onGpsOpenStatus(p0: Boolean) {
    }

    override fun onNaviInfoUpdate(p0: NaviInfo?) {
    }

    override fun updateCameraInfo(p0: Array<out AMapNaviCameraInfo>?) {
    }

    override fun updateIntervalCameraInfo(
        p0: AMapNaviCameraInfo?,
        p1: AMapNaviCameraInfo?,
        p2: Int
    ) {
    }

    override fun onServiceAreaUpdate(p0: Array<out AMapServiceAreaInfo>?) {
    }

    override fun showCross(p0: AMapNaviCross?) {
    }

    override fun hideCross() {
    }

    override fun showModeCross(p0: AMapModelCross?) {
    }

    override fun hideModeCross() {
    }

    override fun showLaneInfo(p0: Array<out AMapLaneInfo>?, p1: ByteArray?, p2: ByteArray?) {
    }

    override fun showLaneInfo(p0: AMapLaneInfo?) {
    }

    override fun hideLaneInfo() {
    }

    override fun onCalculateRouteSuccess(p0: IntArray?) {

    }

    /**
     * 线路规划成功
     */
    override fun onCalculateRouteSuccess(result: AMapCalcRouteResult?) {
        LogHelper.i(TAG, "mAMapNavi.naviPath.coordList===>${mAMapNavi.naviPath.coordList.size}")
        RouterManager.mCoordList = mAMapNavi.naviPath.coordList
        val naviRouteOverlay = NaviRouteOverlay(mAMap, mAMapNavi.naviPath)
        naviRouteOverlay.setShowDefaultLineArrow(true)
        naviRouteOverlay.addToMap()
//        naviRouteOverlay.removeFromMap()
        mAMapNavi.setEmulatorNaviSpeed(10)
        mAMapNavi.startNavi(NaviType.EMULATOR)

    }

    override fun notifyParallelRoad(p0: Int) {
    }

    override fun OnUpdateTrafficFacility(p0: Array<out AMapNaviTrafficFacilityInfo>?) {
    }

    override fun OnUpdateTrafficFacility(p0: AMapNaviTrafficFacilityInfo?) {
    }

    override fun updateAimlessModeStatistics(p0: AimLessModeStat?) {
    }

    override fun updateAimlessModeCongestionInfo(p0: AimLessModeCongestionInfo?) {
    }

    override fun onPlayRing(p0: Int) {
    }

    override fun onNaviRouteNotify(p0: AMapNaviRouteNotifyData?) {
    }

    override fun onGpsSignalWeak(p0: Boolean) {
    }
}