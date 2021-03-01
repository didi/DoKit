package com.didichuxing.doraemonkit.kit.lbs.route

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.core.view.children
import com.amap.api.maps.AMap
import com.amap.api.maps.model.BitmapDescriptorFactory
import com.amap.api.maps.model.MarkerOptions
import com.amap.api.navi.AMapNavi
import com.amap.api.navi.enums.PathPlanningStrategy
import com.amap.api.navi.model.NaviLatLng
import com.blankj.utilcode.util.ConvertUtils
import com.didichuxing.doraemonkit.DoraemonKit
import com.didichuxing.doraemonkit.R
import com.didichuxing.doraemonkit.kit.core.AbsDokitView
import com.didichuxing.doraemonkit.kit.core.DokitViewLayoutParams
import com.didichuxing.doraemonkit.kit.core.DokitViewManager
import com.didichuxing.doraemonkit.kit.lbs.common.AMapUtil

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2/25/21-14:44
 * 描    述：
 * 修订历史：
 * ================================================
 */
class RouteKitView : AbsDokitView() {
    companion object {
        const val TAG = "RouteKitView"
    }

    private var aMap: AMap? = null
    private val mStartPoint = NaviLatLng(39.942295, 116.335891) //起点，116.335891,39.942295

    private val mEndPoint = NaviLatLng(39.995576, 116.481288) //终点，116.481288,39.995576

    private var mAMapNavi: AMapNavi? = null

    override fun onCreate(context: Context?) {
    }

    override fun onCreateView(context: Context?, rootView: FrameLayout?): View {
        return LayoutInflater.from(context).inflate(R.layout.dk_float_lbs_route, rootView, false)
    }

    override fun onViewCreated(rootView: FrameLayout?) {
        rootView?.let {
            val close = it.findViewById<ImageView>(R.id.iv_close)
            close.setOnClickListener {
                DokitViewManager.getInstance().detach(this)
            }

        }



//        setFromAndEndMarker()

    }

    override fun initDokitViewLayoutParams(params: DokitViewLayoutParams?) {
        params?.let {
            it.width = ConvertUtils.dp2px(300.0f)
            it.height = DokitViewLayoutParams.WRAP_CONTENT
            it.gravity = Gravity.TOP or Gravity.LEFT
            it.x = 200
            it.y = 200
        }
    }


    /**
     * 设置起始点marker
     */
//    private fun setFromAndEndMarker() {
//        aMap?.addMarker(
//            MarkerOptions()
//                .position(AMapUtil.convertToLatLng(mStartPoint))
//                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.dk_lbs_start))
//        )
//
//        aMap?.addMarker(
//            MarkerOptions()
//                .position(AMapUtil.convertToLatLng(mEndPoint))
//                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.dk_lbs_end))
//        )
//    }


    override fun onResume() {
        super.onResume()
        aMap = findAMapView()?.map
        mAMapNavi = AMapNavi.getInstance(DoraemonKit.APPLICATION)
        val startList = mutableListOf<NaviLatLng>()
        startList.add(mStartPoint)
        val endList = mutableListOf<NaviLatLng>()
        endList.add(mEndPoint)
        mAMapNavi?.calculateDriveRoute(
            startList,
            endList,
            null,
            PathPlanningStrategy.DRIVING_MULTIPLE_ROUTES_DEFAULT
        )
        mAMapNavi?.addAMapNaviListener(DefaultNaviListener(mAMapNavi))
    }

    private fun findAMapView(): com.amap.api.maps.MapView? {
        val decorView = activity.window.decorView as ViewGroup
        decorView.children.forEach {
            when (it) {
                is com.amap.api.maps.MapView -> return it
                is ViewGroup -> return traversAMapView(it)
            }

        }
        return null
    }

    private fun traversAMapView(viewGroup: ViewGroup): com.amap.api.maps.MapView? {
        viewGroup.children.forEach {
            when (it) {
                is com.amap.api.maps.MapView -> return it
                is ViewGroup -> return traversAMapView(it)
            }

        }
        return null
    }


}