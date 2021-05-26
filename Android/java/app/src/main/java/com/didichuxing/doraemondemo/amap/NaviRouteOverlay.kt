package com.didichuxing.doraemondemo.amap

import android.content.Context
import android.graphics.BitmapFactory
import com.amap.api.maps.AMap
import com.amap.api.maps.model.BitmapDescriptorFactory
import com.amap.api.maps.model.LatLng
import com.amap.api.maps.model.Polyline
import com.amap.api.maps.model.PolylineOptions
import com.amap.api.navi.model.AMapNaviPath
import com.amap.api.navi.model.RouteOverlayOptions
import com.amap.api.navi.view.RouteOverLay
import com.blankj.utilcode.util.ConvertUtils
import com.blankj.utilcode.util.ReflectUtils

/**
 * 地图上的导航路径
 *
 * @author vinda
 * @since 2017/12/27
 */
class NaviRouteOverlay(
    private val aMap: AMap,
    aMapNaviPath: AMapNaviPath?,
    val mContext: Context
) : RouteOverLay(
    aMap, aMapNaviPath, mContext
) {
    var routeId = 0

    //显示默认路线箭头
    private var showDefaultLineArrow = false

    /**
     * 非选中状态，使用比较浅的颜色，需要重新调用addToMap生效
     *
     * @param shadow
     */
    private fun setupOptions(shadow: Boolean) {
        val options: RouteOverlayOptions
        if (shadow) {
            options = customShadowRouteTexture()
            //setZindex(ROUTE_SHADOW_Z_INDEX)
        } else {
            options = customRouteTexture()
            //setZindex(ROUTE_NORMAL_Z_INDEX)
        }
        options.setOnRouteCameShow(false)
        routeOverlayOptions = options
    }

    /**
     * 设置是否显示默认路线（非路况）上的箭头,在调用addToMap之前调用
     */
    fun setShowDefaultLineArrow(visible: Boolean) {
        showDefaultLineArrow = visible
    }

    /**
     * 复写父类方法
     */
    override fun addToMap() {
        super.addToMap()
        //支持默认路线时显示箭头
        if (showDefaultLineArrow) {
            //显示箭头
            addDefaultArrowLine()
        }
    }

    /**
     * 默认路线加入箭头
     */
    private fun addDefaultArrowLine() {
        //通过反射的方式向父类加入箭头路线
        val path = ReflectUtils.reflect(this).field("mPathPoints").get<List<Any>>()
        val lines = ReflectUtils.reflect(this).field("mCustomPolyLines").get<List<Any>>()
        if (path != null && path is List<*> && lines != null && lines is List<*>) {
            val mLatLngsOfPath = path as List<LatLng>
            val mCustomPolylines = lines as MutableList<Polyline>
            if (mLatLngsOfPath.isNotEmpty()) {
                val arrowOnRoute = BitmapDescriptorFactory.fromBitmap(
                    routeOverlayOptions.arrowOnTrafficRoute
                )
                val mDefaultArrowline = this.aMap.addPolyline(
                    PolylineOptions().addAll(mLatLngsOfPath).setCustomTexture(arrowOnRoute).width(
                        width / 1.5f
                    )
                )
                mCustomPolylines.add(mDefaultArrowline)
            }
        }
    }

    private fun customShadowRouteTexture(): RouteOverlayOptions {
        val routeOverlayOptions = RouteOverlayOptions()
        var fis =
            BitmapDescriptorFactory::class.java.getResourceAsStream("/assets/img/dk_custtexture_aolr.png")
        val custtexture_aolr = BitmapFactory.decodeStream(fis)
        fis =
            BitmapDescriptorFactory::class.java.getResourceAsStream("/assets/img/dk_custtexture_b.png")
        val custtexture = BitmapFactory.decodeStream(fis)
        fis =
            BitmapDescriptorFactory::class.java.getResourceAsStream("/assets/img/dk_custtexture_no_b.png")
        val custtexture_no = BitmapFactory.decodeStream(fis)
        fis =
            BitmapDescriptorFactory::class.java.getResourceAsStream("/assets/img/dk_custtexture_green_b.png")
        val custtexture_green = BitmapFactory.decodeStream(fis)
        fis =
            BitmapDescriptorFactory::class.java.getResourceAsStream("/assets/img/dk_custtexture_slow_b.png")
        val custtexture_slow = BitmapFactory.decodeStream(fis)
        fis =
            BitmapDescriptorFactory::class.java.getResourceAsStream("/assets/img/dk_custtexture_bad_b.png")
        val custtexture_bad = BitmapFactory.decodeStream(fis)
        fis =
            BitmapDescriptorFactory::class.java.getResourceAsStream("/assets/img/dk_custtexture_grayred_b.png")
        val custtexture_grayred = BitmapFactory.decodeStream(fis)
        routeOverlayOptions.arrowOnTrafficRoute = custtexture_aolr
        routeOverlayOptions.normalRoute = custtexture
        routeOverlayOptions.unknownTraffic = custtexture_no
        routeOverlayOptions.smoothTraffic = custtexture_green
        routeOverlayOptions.slowTraffic = custtexture_slow
        routeOverlayOptions.jamTraffic = custtexture_bad
        routeOverlayOptions.veryJamTraffic = custtexture_grayred
        //设置导航线路的宽度, 单位：像素
        routeOverlayOptions.lineWidth =
            ConvertUtils.dp2px(LINE_WIDTH_DP.toFloat()).toFloat()
        return routeOverlayOptions
    }

    companion object {
        private const val LINE_WIDTH_DP = 14
        fun customRouteTexture(): RouteOverlayOptions {
            val routeOverlayOptions = RouteOverlayOptions()
            var fis =
                BitmapDescriptorFactory::class.java.getResourceAsStream("/assets/img/dk_custtexture_aolr.png")
            val custtexture_aolr = BitmapFactory.decodeStream(fis)
            fis =
                BitmapDescriptorFactory::class.java.getResourceAsStream("/assets/img/dk_custtexture.png")
            val custtexture = BitmapFactory.decodeStream(fis)
            fis =
                BitmapDescriptorFactory::class.java.getResourceAsStream("/assets/img/dk_custtexture_no.png")
            val custtexture_no = BitmapFactory.decodeStream(fis)
            fis =
                BitmapDescriptorFactory::class.java.getResourceAsStream("/assets/img/dk_custtexture_green.png")
            val custtexture_green = BitmapFactory.decodeStream(fis)
            fis =
                BitmapDescriptorFactory::class.java.getResourceAsStream("/assets/img/dk_custtexture_slow.png")
            val custtexture_slow = BitmapFactory.decodeStream(fis)
            fis =
                BitmapDescriptorFactory::class.java.getResourceAsStream("/assets/img/dk_custtexture_bad.png")
            val custtexture_bad = BitmapFactory.decodeStream(fis)
            fis =
                BitmapDescriptorFactory::class.java.getResourceAsStream("/assets/img/dk_custtexture_grayred.png")
            val custtexture_grayred = BitmapFactory.decodeStream(fis)
            routeOverlayOptions.arrowOnTrafficRoute = custtexture_aolr
            routeOverlayOptions.normalRoute = custtexture
            routeOverlayOptions.unknownTraffic = custtexture_no
            routeOverlayOptions.smoothTraffic = custtexture_green
            routeOverlayOptions.slowTraffic = custtexture_slow
            routeOverlayOptions.jamTraffic = custtexture_bad
            routeOverlayOptions.veryJamTraffic = custtexture_grayred
            //设置导航线路的宽度, 单位：像素
            routeOverlayOptions.lineWidth = ConvertUtils.dp2px(LINE_WIDTH_DP.toFloat()).toFloat()
            return routeOverlayOptions
        }
    }

    init {
        setupOptions(false)
        //不显示起点、终点
//        setStartPointBitmap(Bitmap.createBitmap(1, 1, Bitmap.Config.ALPHA_8))
//        setEndPointBitmap(Bitmap.createBitmap(1, 1, Bitmap.Config.ALPHA_8))
    }
}