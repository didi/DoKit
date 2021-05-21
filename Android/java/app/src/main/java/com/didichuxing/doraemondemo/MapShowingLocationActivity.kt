//package com.didichuxing.doraemondemo
//
//import android.Manifest
//import android.annotation.SuppressLint
//import android.os.Bundle
//import android.util.Log
//import android.view.View
//import androidx.appcompat.app.AppCompatActivity
//import com.baidu.location.BDAbstractLocationListener
//import com.baidu.location.BDLocation
//import com.baidu.location.LocationClient
//import com.baidu.location.LocationClientOption
//import com.didichuxing.doraemonkit.kit.core.ViewSetupHelper
//import com.tencent.tencentmap.mapsdk.maps.CameraUpdateFactory
//import com.tencent.tencentmap.mapsdk.maps.SupportMapFragment
//import com.tencent.tencentmap.mapsdk.maps.TencentMap
//import com.tencent.tencentmap.mapsdk.maps.model.*
//import pub.devrel.easypermissions.EasyPermissions
//import pub.devrel.easypermissions.PermissionRequest
//
//
//class MapShowingLocationActivity : AppCompatActivity() {
//    private lateinit var mRootView: View
//    private var mTencentMap: TencentMap? = null
//    private var mBaiduLocationClient: LocationClient? = null
//    private var mCustomMarker: Marker? = null
//    private var mLocationMarker: Marker? = null
//    private var mAccuracyCircle: Circle? = null
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_map_showing_location)
//        mRootView = findViewById<View>(R.id.map_showing_location)
//        initMap()
//        initButtons()
//        EasyPermissions.requestPermissions(
//            PermissionRequest.Builder(
//                this, 200,
//                Manifest.permission.ACCESS_FINE_LOCATION,
//                Manifest.permission.ACCESS_COARSE_LOCATION,
//                Manifest.permission.READ_EXTERNAL_STORAGE,
//                Manifest.permission.WRITE_EXTERNAL_STORAGE
//            ).build()
//        )
//    }
//
//    private fun initMap() {
//        val manager = supportFragmentManager
//        val fragment = manager.findFragmentById(R.id.fragment_map) as SupportMapFragment?
//        if (fragment != null) {
//            mTencentMap = fragment.map
//        }
//    }
//
//
//    private fun initButtons() {
//        ViewSetupHelper.setupButton(mRootView, R.id.map_test_btn_1, "添加") {
//            setMarker(40.011313, 116.391907)
//        }
//        ViewSetupHelper.setupButton(mRootView, R.id.map_test_btn_2, "移除") {
//            removeMarker()
//        }
//        ViewSetupHelper.setupButton(mRootView, R.id.map_test_btn_3, "归位") {
//            if (mCustomMarker != null) mTencentMap?.animateCamera(CameraUpdateFactory.newLatLng(mCustomMarker?.position))
//        }
//        ViewSetupHelper.setupButton(mRootView, R.id.map_test_btn_4, "启动定位") {
//            startLocation()
//        }
//        ViewSetupHelper.setupButton(mRootView, R.id.map_test_btn_5, "停止定位") {
//            stopLocation()
//        }
//    }
//
//    private var mbdLocationListener: BDAbstractLocationListener =
//        object : BDAbstractLocationListener() {
//            override fun onReceiveLocation(bdLocation: BDLocation) {
//                Log.i(TAG, "百度定位===onReceiveLocation===lat==>" + bdLocation.latitude + "   lng==>" + bdLocation.longitude)
//                setLocationMarker(bdLocation.latitude, bdLocation.longitude, 100f.toDouble())
//            }
//        }
//
//    @SuppressLint("MissingPermission")
//    private fun startLocation() {
//        //百度地图
//        if (mBaiduLocationClient == null) {
//            mBaiduLocationClient = LocationClient(this)
//            //通过LocationClientOption设置LocationClient相关参数
//            val option = LocationClientOption()
//            // 打开gps
//            option.isOpenGps = true
//            // 设置坐标类型
//            option.setCoorType("gcj02")
//            option.setScanSpan(5000)
//            mBaiduLocationClient!!.locOption = option
//            mBaiduLocationClient!!.registerLocationListener(mbdLocationListener)
//        }
//        mBaiduLocationClient?.start()
//    }
//
//    private fun stopLocation() {
//        if (mBaiduLocationClient == null) return
//        mBaiduLocationClient?.stop()
//    }
//
//    private fun setMarker(lat: Double, lng: Double) {
//        val position = LatLng(lat, lng)
//        mCustomMarker?.remove()
//        mCustomMarker = mTencentMap?.addMarker(MarkerOptions(position))
//        mTencentMap?.animateCamera(CameraUpdateFactory.newLatLng(position))
//    }
//
//    private fun setLocationMarker(lat: Double, lng: Double) {
//        val position = LatLng(lat, lng)
//        if (mLocationMarker == null) {
//            val markerOptions = MarkerOptions(position)
//                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.dk_location_marker))
//            mLocationMarker = mTencentMap?.addMarker(markerOptions)
//        } else {
//            mLocationMarker?.position = position
//        }
//        mTencentMap?.animateCamera(CameraUpdateFactory.newLatLng(position))
//    }
//
//    private fun setLocationMarker(lat: Double, lng: Double, radius: Double) {
//        val position = LatLng(lat, lng)
//        if (mLocationMarker == null) {
//            val markerOptions = MarkerOptions(position)
//                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.dk_location_marker))
//            mLocationMarker = mTencentMap?.addMarker(markerOptions)
//            mAccuracyCircle = mTencentMap?.addCircle(
//                CircleOptions().center(position)
//                    .radius(radius)
//                    .fillColor(resources.getColor(R.color.colorCircleMarkerFill))
//                    .strokeColor(resources.getColor(R.color.colorCircleMarkerStroke))
//            )
//        } else {
//            mLocationMarker?.position = position
//            mAccuracyCircle?.center = position
//            mAccuracyCircle?.radius = radius
//        }
//        mTencentMap?.animateCamera(CameraUpdateFactory.newLatLng(position))
//    }
//
//
//    private fun removeMarker() {
//        mCustomMarker?.remove()
//        mCustomMarker = null
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        stopLocation()
//    }
//
//    companion object {
//        const val TAG = "MapShowingLocationActivity"
//    }
//}