//package com.didichuxing.doraemondemo
//
//import android.location.Location
//import android.os.Bundle
//import android.os.Looper
//import android.widget.Toast
//import androidx.appcompat.app.AppCompatActivity
//import com.amap.api.location.AMapLocationClient
//import com.amap.api.location.AMapLocationClientOption
//import com.amap.api.maps.AMap
//import com.amap.api.maps.CameraUpdateFactory
//import com.amap.api.maps.model.LatLng
//import com.amap.api.maps.model.MarkerOptions
//import com.amap.api.maps.model.MyLocationStyle
//import com.amap.api.services.route.*
//import com.baidu.location.*
//import com.baidu.mapapi.map.*
//import com.tencent.map.geolocation.TencentLocation
//import com.tencent.map.geolocation.TencentLocationListener
//import com.tencent.map.geolocation.TencentLocationManager
//import com.tencent.map.geolocation.TencentLocationRequest
//import com.tencent.tencentmap.mapsdk.maps.LocationSource
//import com.tencent.tencentmap.mapsdk.maps.TencentMap
//import kotlinx.android.synthetic.main.activity_amap_path.*
//import kotlinx.android.synthetic.main.activity_map.*
//
//
///**
// * 高德地图路径规划
// */
//class MapActivity : AppCompatActivity() {
//
//
//    companion object {
//        val TAG = "MapActivity"
//        const val ZOOM_INDEX = 14.0
//        const val BD_ZOOM_INDEX = 18.0
//    }
//
//    private lateinit var aMap: AMap
//
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_map)
//        amap_view.onCreate(savedInstanceState)
//        aMap = amap_view.map
//        aMap.minZoomLevel = ZOOM_INDEX.toFloat()
//        initAMapLocation()
//        initTencentMapLocation()
//        initBDMapLocation()
//    }
//
//
////    lateinit var aMapLocationClient: AMapLocationClient
////    lateinit var aMapLocationClientOption: AMapLocationClientOption
//
//    /**
//     * 初始化高德地图的定位
//     */
//    private fun initAMapLocation() {
////        aMapLocationClient = AMapLocationClient(this)
////        aMapLocationClientOption = AMapLocationClientOption()
////        aMapLocationClientOption.locationMode =
////            AMapLocationClientOption.AMapLocationMode.Hight_Accuracy
////        aMapLocationClientOption.interval = 2000
////        aMapLocationClient.setLocationOption(aMapLocationClientOption)
////        aMapLocationClient.setLocationListener {
////            val options = MarkerOptions().position(LatLng(it.latitude, it.longitude))
////            options.draggable(true).icon(
////                com.amap.api.maps.model.BitmapDescriptorFactory.fromResource(
////                    R.mipmap.ic_navi_map_gps_locked
////                )
////            )
////
////            aMap.addMarker(options)
////            aMap.clear()
////            aMap.animateCamera(CameraUpdateFactory.newLatLng(LatLng(it.latitude, it.longitude)))
////        }
////        aMapLocationClient.startLocation()
//
//        //初始化定位蓝点样式类myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);//连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）如果不设置myLocationType，默认也会执行此种模式。
//        val myLocationStyle = MyLocationStyle()
//        //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒
//        myLocationStyle.interval(2000)
//        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATE)
//        myLocationStyle.myLocationIcon(
//            com.amap.api.maps.model.BitmapDescriptorFactory.fromResource(
//                R.mipmap.ic_navi_map_gps_locked
//            )
//        )
//        ////设置是否显示定位小蓝点，用于满足只想使用定位，不想使用定位小蓝点的场景，设置false以后图面上不再有定位蓝点的概念，但是会持续回调位置信息。
//        myLocationStyle.showMyLocation(true)
//        aMap.myLocationStyle = myLocationStyle //设置定位蓝点的Style
//
//        //aMap.getUiSettings().setMyLocationButtonEnabled(true);设置默认定位按钮是否显示，非必需设置。
//        // 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。
//        aMap.isMyLocationEnabled = true
//
//    }
//
//    /**
//     * 百度地图回调
//     */
//    private lateinit var mBDLocationClient: LocationClient
//
//    private val mBDLocationListener: BDLocationListener =
//        BDLocationListener { location ->
//            location?.let {
//                val locData: MyLocationData = MyLocationData.Builder()
//                    .accuracy(location.radius) // 此处设置开发者获取到的方向信息，顺时针0-360
//                    .direction(location.direction)
//                    .latitude(location.latitude)
//                    .longitude(location.longitude)
//                    .build()
//                bdmap_view.map.setMyLocationData(locData)
//            }
//        }
//
//
//    lateinit var mBDMap: BaiduMap
//
//    /**
//     * 初始化百度地图的定位
//     */
//    private fun initBDMapLocation() {
//        mBDMap = bdmap_view.map
//        mBDMap.isMyLocationEnabled = true
//        val mapStatus: MapStatus = MapStatus.Builder()
//            .zoom(BD_ZOOM_INDEX.toFloat())
//            .build()
//        mBDMap.setMapStatus(MapStatusUpdateFactory.newMapStatus(mapStatus))
//        mBDMap.setMyLocationConfiguration(
//            MyLocationConfiguration(
//                MyLocationConfiguration.LocationMode.FOLLOWING,
//                false,
//                BitmapDescriptorFactory.fromResource(R.mipmap.ic_navi_map_gps_locked)
//            )
//        )
//        //定位初始化
//        mBDLocationClient = LocationClient(this)
//
//        //通过LocationClientOption设置LocationClient相关参数
//        val option = LocationClientOption()
//        option.isOpenGps = true // 打开gps
//        option.setCoorType("bd09ll") // 设置坐标类型
////        option.setScanSpan(5000)
//
//        //设置locationClientOption
//        mBDLocationClient.locOption = option
//        //注册LocationListener监听器
//        mBDLocationClient.registerLocationListener(mBDLocationListener)
//        //开启地图定位图层
//        mBDLocationClient.start()
//
//
//    }
//
//
//    /**
//     * ========腾讯地图========
//     */
//
//    //用于访问腾讯定位服务的类, 周期性向客户端提供位置更新
//    var mTencentLocationManager: TencentLocationManager? = null
//
//    //创建定位请求
//    var mTencentLocationRequest: TencentLocationRequest? = TencentLocationRequest.create()
//    var mTencentOnLocationChangedListener: LocationSource.OnLocationChangedListener? = null
//    val mTencentLocationListener: TencentLocationListener by lazy {
//        object : TencentLocationListener {
//            override fun onLocationChanged(
//                tencentLocation: TencentLocation?,
//                code: Int,
//                s: String?
//            ) {
//                tencentLocation?.let {
//                    if (code == TencentLocation.ERROR_OK) {
//                        val location = Location(tencentLocation.provider)
//                        //设置经纬度
//                        //设置经纬度
//                        location.latitude = tencentLocation.latitude
//                        location.longitude = tencentLocation.longitude
//                        //设置精度，这个值会被设置为定位点上表示精度的圆形半径
//                        //设置精度，这个值会被设置为定位点上表示精度的圆形半径
//                        location.accuracy = tencentLocation.accuracy
//                        //设置定位标的旋转角度，注意 tencentLocation.getBearing() 只有在 gps 时才有可能获取
//                        //设置定位标的旋转角度，注意 tencentLocation.getBearing() 只有在 gps 时才有可能获取
//                        location.bearing = tencentLocation.bearing
//                        //将位置信息返回给地图
//                        //将位置信息返回给地图
//                        mTencentOnLocationChangedListener?.onLocationChanged(location)
//                    }
//                }
//            }
//
//            override fun onStatusUpdate(p0: String?, p1: Int, p2: String?) {
//            }
//
//        }
//    }
//
//    /**
//     * ========腾讯地图========
//     */
//
//
//    lateinit var mTencentMap: TencentMap
//
//    /**
//     * 初始化腾讯地图的定位
//     */
//    private fun initTencentMapLocation() {
//        mTencentLocationManager = TencentLocationManager.getInstance(this)
//        mTencentMap = tencentmap_view.map
//        mTencentMap.setLocationSource(object : LocationSource {
//            override fun activate(locationChangedListener: LocationSource.OnLocationChangedListener?) {
//                locationChangedListener?.let {
//                    mTencentOnLocationChangedListener = it
//                    //开启定位
//                    //开启定位
//                    val err: Int? = mTencentLocationManager?.requestLocationUpdates(
//                        mTencentLocationRequest, mTencentLocationListener, Looper.myLooper()
//                    )
//                    when (err) {
//                        1 -> Toast.makeText(
//                            this@MapActivity,
//                            "设备缺少使用腾讯定位服务需要的基本条件",
//                            Toast.LENGTH_SHORT
//                        ).show()
//                        2 -> Toast.makeText(
//                            this@MapActivity,
//                            "manifest 中配置的 key 不正确", Toast.LENGTH_SHORT
//                        ).show()
//                        3 -> Toast.makeText(
//                            this@MapActivity,
//                            "自动加载libtencentloc.so失败", Toast.LENGTH_SHORT
//                        ).show()
//                        else -> {
//                        }
//                    }
//                }
//            }
//
//            override fun deactivate() {
//                //当不需要展示定位点时，需要停止定位并释放相关资源
//                mTencentLocationManager?.removeUpdates(mTencentLocationListener);
//                mTencentLocationManager = null;
//                mTencentLocationRequest = null;
//                mTencentLocationManager = null;
//            }
//
//        })
//        mTencentMap.isMyLocationEnabled = true
//        mTencentMap.setMinZoomLevel(ZOOM_INDEX.toInt())
//
//        //设置定位周期（位置监听器回调周期）为3s
////        mTencentLocationRequest?.interval = 3000
//        mTencentLocationManager?.requestSingleFreshLocation(
//            mTencentLocationRequest,
//            mTencentLocationListener, Looper.myLooper()
//        )
//    }
//
//    override fun onStart() {
//        tencentmap_view.onStart()
//        super.onStart()
//    }
//
//    override fun onResume() {
//        tencentmap_view.onResume()
//        amap_view.onResume()
//        bdmap_view.onResume()
//        super.onResume()
//
//    }
//
//    override fun onPause() {
//        tencentmap_view.onPause()
//        amap_view.onPause()
//        bdmap_view.onPause()
//        super.onPause()
//    }
//
//    override fun onStop() {
//        tencentmap_view.onStop()
//        super.onStop()
//    }
//
//    override fun onDestroy() {
//        tencentmap_view.onDestroy()
//        amap_view.onDestroy()
//        mBDLocationClient.stop()
//        bdmap_view.onDestroy()
//        bdmap_view.map.isMyLocationEnabled = false
//        super.onDestroy()
//
//    }
//
//    override fun onSaveInstanceState(outState: Bundle) {
//        amap_view.onSaveInstanceState(outState)
//        super.onSaveInstanceState(outState)
//    }
//}
