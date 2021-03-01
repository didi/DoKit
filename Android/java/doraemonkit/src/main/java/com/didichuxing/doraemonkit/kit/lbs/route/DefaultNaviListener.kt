package com.didichuxing.doraemonkit.kit.lbs.route

import com.amap.api.navi.AMapNavi
import com.amap.api.navi.AMapNaviListener
import com.amap.api.navi.enums.NaviType
import com.amap.api.navi.model.*
import com.didichuxing.doraemonkit.DoraemonKit
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
class DefaultNaviListener(val mAMapNavi: AMapNavi?) : AMapNaviListener {

    companion object {
        const val TAG = "DefaultNaviListener"
    }

    override fun onInitNaviFailure() {
        TODO("Not yet implemented")
    }

    override fun onInitNaviSuccess() {
        TODO("Not yet implemented")
    }

    override fun onStartNavi(p0: Int) {
        TODO("Not yet implemented")
    }

    override fun onTrafficStatusUpdate() {
        TODO("Not yet implemented")
    }

    override fun onLocationChange(p0: AMapNaviLocation?) {
        TODO("Not yet implemented")
    }

    override fun onGetNavigationText(p0: Int, p1: String?) {
        TODO("Not yet implemented")
    }

    override fun onGetNavigationText(p0: String?) {
        TODO("Not yet implemented")
    }

    override fun onEndEmulatorNavi() {
        TODO("Not yet implemented")
    }

    override fun onArriveDestination() {
        TODO("Not yet implemented")
    }

    override fun onCalculateRouteFailure(p0: Int) {
        TODO("Not yet implemented")
    }

    override fun onCalculateRouteFailure(p0: AMapCalcRouteResult?) {
        TODO("Not yet implemented")
    }

    override fun onReCalculateRouteForYaw() {
        TODO("Not yet implemented")
    }

    override fun onReCalculateRouteForTrafficJam() {
        TODO("Not yet implemented")
    }

    override fun onArrivedWayPoint(p0: Int) {
        TODO("Not yet implemented")
    }

    override fun onGpsOpenStatus(p0: Boolean) {
        TODO("Not yet implemented")
    }

    override fun onNaviInfoUpdate(p0: NaviInfo?) {
        TODO("Not yet implemented")
    }

    override fun updateCameraInfo(p0: Array<out AMapNaviCameraInfo>?) {
        TODO("Not yet implemented")
    }

    override fun updateIntervalCameraInfo(
        p0: AMapNaviCameraInfo?,
        p1: AMapNaviCameraInfo?,
        p2: Int
    ) {
        TODO("Not yet implemented")
    }

    override fun onServiceAreaUpdate(p0: Array<out AMapServiceAreaInfo>?) {
        TODO("Not yet implemented")
    }

    override fun showCross(p0: AMapNaviCross?) {
        TODO("Not yet implemented")
    }

    override fun hideCross() {
        TODO("Not yet implemented")
    }

    override fun showModeCross(p0: AMapModelCross?) {
        TODO("Not yet implemented")
    }

    override fun hideModeCross() {
        TODO("Not yet implemented")
    }

    override fun showLaneInfo(p0: Array<out AMapLaneInfo>?, p1: ByteArray?, p2: ByteArray?) {
        TODO("Not yet implemented")
    }

    override fun showLaneInfo(p0: AMapLaneInfo?) {
        TODO("Not yet implemented")
    }

    override fun hideLaneInfo() {
        TODO("Not yet implemented")
    }

    override fun onCalculateRouteSuccess(p0: IntArray?) {

    }

    /**
     * 线路规划成功
     */
    override fun onCalculateRouteSuccess(result: AMapCalcRouteResult?) {
//        result?.let {
//            val naviPath = AMapNavi.getInstance(DoraemonKit.APPLICATION).naviPaths
//            naviPath.forEach {
//                it.value.coordList.forEach { naviLatLng ->
//                    LogHelper.i(TAG, "naviLatLng========>$naviLatLng")
//                }
//            }
//        }
        mAMapNavi?.startNavi(NaviType.EMULATOR)

    }

    override fun notifyParallelRoad(p0: Int) {
        TODO("Not yet implemented")
    }

    override fun OnUpdateTrafficFacility(p0: Array<out AMapNaviTrafficFacilityInfo>?) {
        TODO("Not yet implemented")
    }

    override fun OnUpdateTrafficFacility(p0: AMapNaviTrafficFacilityInfo?) {
        TODO("Not yet implemented")
    }

    override fun updateAimlessModeStatistics(p0: AimLessModeStat?) {
        TODO("Not yet implemented")
    }

    override fun updateAimlessModeCongestionInfo(p0: AimLessModeCongestionInfo?) {
        TODO("Not yet implemented")
    }

    override fun onPlayRing(p0: Int) {
        TODO("Not yet implemented")
    }

    override fun onNaviRouteNotify(p0: AMapNaviRouteNotifyData?) {
        TODO("Not yet implemented")
    }

    override fun onGpsSignalWeak(p0: Boolean) {
        TODO("Not yet implemented")
    }
}