package com.didichuxing.doraemondemo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.amap.api.maps.AMap
import com.amap.api.maps.model.BitmapDescriptorFactory
import com.amap.api.maps.model.MarkerOptions
import com.amap.api.services.core.AMapException
import com.amap.api.services.core.LatLonPoint
import com.amap.api.services.route.*
import com.amap.api.services.route.RouteSearch.DriveRouteQuery
import com.amap.api.services.route.RouteSearch.FromAndTo
import com.didichuxing.doraemondemo.amap.AMapUtil
import com.didichuxing.doraemondemo.amap.DrivingRouteOverLay
import com.didichuxing.doraemonkit.util.LogHelper
import kotlinx.android.synthetic.main.activity_amap_path.*


/**
 * 高德地图路径规划
 */
class AmapPathActivity : AppCompatActivity(), RouteSearch.OnRouteSearchListener {
    lateinit var aMap: AMap
    lateinit var mRouteSearch: RouteSearch
    private val mStartPoint = LatLonPoint(39.942295, 116.335891) //起点，116.335891,39.942295

    private val mEndPoint = LatLonPoint(39.995576, 116.481288) //终点，116.481288,39.995576

    companion object {
        val TAG = "AmapPathActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_amap_path)
        amap_view.onCreate(savedInstanceState)
        aMap = amap_view.map
        //初始化定位蓝点样式类myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);
        // 连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。
        // （1秒1次定位）如果不设置myLocationType，默认也会执行此种模式。
//        val myLocationStyle = MyLocationStyle()
//
//        myLocationStyle.interval(2000) //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
//        aMap.setMyLocationStyle(myLocationStyle) //设置定位蓝点的Style
//        aMap.isMyLocationEnabled = true

        mRouteSearch = RouteSearch(this)
        mRouteSearch.setRouteSearchListener(this)
        setFromAndEndMarker()
        searchRouteResult()
    }

    /**
     * 设置起始点marker
     */
    private fun setFromAndEndMarker() {
        aMap.addMarker(
            MarkerOptions()
                .position(AMapUtil.convertToLatLng(mStartPoint))
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.start))
        )

        aMap.addMarker(
            MarkerOptions()
                .position(AMapUtil.convertToLatLng(mEndPoint))
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.end))
        )
    }

    /**
     * 计算路径规划
     */
    private fun searchRouteResult() {
        val fromAndTo = FromAndTo(mStartPoint, mEndPoint)
        // 驾车路径规划
        val query = DriveRouteQuery(
            fromAndTo, RouteSearch.DrivingDefault, null,
            null, ""
        ) // 第一个参数表示路径规划的起点和终点，第二个参数表示驾车模式，第三个参数表示途经点，第四个参数表示避让区域，第五个参数表示避让道路

        mRouteSearch.calculateDriveRouteAsyn(query) // 异步路径规划驾车模式查询

    }

    override fun onDestroy() {
        super.onDestroy()
        amap_view.onDestroy()
    }

    override fun onResume() {
        super.onResume()
        amap_view.onResume()
    }

    override fun onPause() {
        super.onPause()
        amap_view.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        amap_view.onSaveInstanceState(outState)
    }

    override fun onBusRouteSearched(p0: BusRouteResult?, p1: Int) {
        TODO("Not yet implemented")
    }


    /**
     * 驾车路径规划完成
     */
    override fun onDriveRouteSearched(result: DriveRouteResult?, errorCode: Int) {
        LogHelper.i(TAG, "===onDriveRouteSearched===")
        if (errorCode == AMapException.CODE_AMAP_SUCCESS) {
            result?.let {
                val drivePath = it.paths[0]
                val drivingRouteOverlay = DrivingRouteOverLay(
                    this, aMap, drivePath,
                    result.startPos,
                    result.targetPos, null
                )
                drivingRouteOverlay.setNodeIconVisibility(false) //设置节点marker是否显示

                drivingRouteOverlay.setIsColorfulline(true) //是否用颜色展示交通拥堵情况，默认true

                drivingRouteOverlay.removeFromMap()
                drivingRouteOverlay.addToMap()
                drivingRouteOverlay.zoomToSpan()
            }
        }
    }

    override fun onWalkRouteSearched(p0: WalkRouteResult?, p1: Int) {
        TODO("Not yet implemented")
    }

    override fun onRideRouteSearched(p0: RideRouteResult?, p1: Int) {
        TODO("Not yet implemented")
    }


}