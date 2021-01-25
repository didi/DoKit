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
import kotlinx.android.synthetic.main.activity_amap_path.*


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

    }
}