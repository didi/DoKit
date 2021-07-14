package com.didichuxing.doraemondemo.amap

import android.content.Context
import android.util.Log
import com.amap.api.maps.AMap
import com.amap.api.maps.AMapUtils
import com.amap.api.maps.model.LatLng
import com.amap.api.navi.AMapNavi
import com.amap.api.navi.AMapNaviListener
import com.amap.api.navi.enums.NaviType
import com.amap.api.navi.model.*
import io.reactivex.disposables.Disposable

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2/25/21-20:00
 * 描    述：
 * 修订历史：
 * ================================================
 */
class DefaultNaviListener(val mAMap: AMap, val mAMapNavi: AMapNavi, val context: Context) :
    AMapNaviListener {
//    private var mNaviRouteOverlay: NaviRouteOverlay? = null
//
//    init {
//        mNaviRouteOverlay = NaviRouteOverlay(mAMap, null)
//    }

    private var disp: Disposable? = null
    private var mNaviLocation: AMapNaviLocation? = null

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

    /**
     * 改变位置时自动回调
     */
    override fun onLocationChange(location: AMapNaviLocation?) {
        val calculateLineDistance: Float = if (mNaviLocation == null || location == null) -1f else
            AMapUtils.calculateLineDistance(
                LatLng(mNaviLocation!!.coord.latitude, mNaviLocation!!.coord.longitude),
                LatLng(location.coord.latitude, location.coord.longitude)
            )
        if (calculateLineDistance < 0) {
            Log.v(TAG, "⚠️高德定位：" + location.getString())
        } else if (calculateLineDistance > 50) {
            Log.w(TAG, "⚠️高德定位：跳动距离:" + calculateLineDistance + "  " + location.getString())
        } else {
            Log.v(TAG, "⚠️高德定位：跳动距离:" + calculateLineDistance + "  " + location.getString())
        }
        mNaviLocation = location
    }

    private fun AMapNaviLocation?.getString(): String {
        return if (this != null) "lat,lng:${coord?.latitude},:${coord?.longitude}, 精度:$accuracy, 速度:$speed, 方向:$bearing, 海拔:$altitude, 时间:$time"
        else "null"
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
//        LogHelper.i(TAG, "mAMapNavi.naviPath.coordList===>${mAMapNavi.naviPath.coordList.size}")
//        RouterManager.mCoordList = mAMapNavi.naviPath.coordList
        val naviRouteOverlay = NaviRouteOverlay(mAMap, mAMapNavi.naviPath, context)
        naviRouteOverlay.setShowDefaultLineArrow(true)
        naviRouteOverlay.addToMap()
//        naviRouteOverlay.removeFromMap()
        mAMapNavi.setEmulatorNaviSpeed(10)
        /**
         * 	CRUISE
        巡航模式（数值：3）
        EMULATOR
        模拟导航（数值：2）
        GPS
        实时导航（数值：1）
        NONE
        未开始导航（数值：-1）
         */
        mAMapNavi.startNavi(NaviType.GPS)

//        disp = MockGPSTaskManager.startGpsMockTask(mAMapNavi.naviPath)?.subscribe()
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

    fun onDestroy() {
        if (disp != null && !disp!!.isDisposed()) {
            disp!!.dispose()
        }
    }
}