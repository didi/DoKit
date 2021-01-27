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
import com.baidu.mapapi.SDKInitializer
import com.didichuxing.doraemondemo.amap.AMapUtil
import com.didichuxing.doraemondemo.amap.DrivingRouteOverLay
import com.didichuxing.doraemonkit.util.LogHelper
import com.tencent.map.geolocation.TencentLocationManager
import com.tencent.map.geolocation.TencentLocationRequest
import com.tencent.tencentmap.mapsdk.maps.MapView
import kotlinx.android.synthetic.main.activity_amap_path.*
import kotlinx.android.synthetic.main.activity_map.*


/**
 * 高德地图路径规划
 */
class MapActivity : AppCompatActivity() {

    companion object {
        val TAG = "AmapPathActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)
        amap_view.onCreate(savedInstanceState)
    }

    /**
     * 初始化高德地图的定位
     */
    fun initAMapLocation() {

    }

    /**
     * 初始化百度地图的定位
     */
    fun initBDMapLocation() {

    }

    /**
     * 初始化腾讯地图的定位
     */
    fun initTencentMapLocation() {
        tencentmap_view.map.setLocationSource(obje)
        tencentmap_view.map.isMyLocationEnabled = true

        //用于访问腾讯定位服务的类, 周期性向客户端提供位置更新
        val locationManager = TencentLocationManager.getInstance(this)
        //创建定位请求
        val locationRequest = TencentLocationRequest.create()
        //设置定位周期（位置监听器回调周期）为3s
        locationRequest.interval = 3000
        locationManager.requestSingleFreshLocation(locationRequest)
    }

    override fun onStart() {
        super.onStart()
        tencentmap_view.onStart()
    }

    override fun onResume() {
        super.onResume()
        tencentmap_view.onResume()
        amap_view.onResume()
    }

    override fun onPause() {
        super.onPause()
        tencentmap_view.onPause()
        amap_view.onPause()
    }

    override fun onStop() {
        super.onStop()
        tencentmap_view.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        tencentmap_view.onDestroy()
        amap_view.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        amap_view.onSaveInstanceState(outState)
    }
}
