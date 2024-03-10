package com.didichuxing.doraemonkit.gps_mock.lbs.route

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.core.view.children
import com.amap.api.navi.AMapNavi
import com.didichuxing.doraemonkit.DoKit
import com.didichuxing.doraemonkit.R
import com.didichuxing.doraemonkit.gps_mock.gpsmock.GpsMockManager
import com.didichuxing.doraemonkit.kit.core.AbsDoKitView
import com.didichuxing.doraemonkit.kit.core.DoKitViewLayoutParams
import com.didichuxing.doraemonkit.util.ConvertUtils
import com.didichuxing.doraemonkit.util.LogHelper
import kotlin.math.ceil


/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2/25/21-14:44
 * 描    述：
 * 修订历史：
 * ================================================
 */
class AMapRealNavMockView : AbsDoKitView() {
    companion object {
        const val TAG = "RouteKitView"
    }


    private var mAMapNavi: AMapNavi? = null

    override fun onCreate(context: Context?) {
        mAMapNavi = AMapNavi.getInstance(activity.application)
    }

    override fun onCreateView(context: Context?, rootView: FrameLayout?): View {
        return LayoutInflater.from(context).inflate(R.layout.dk_float_lbs_route, rootView, false)
    }

    var index = 0

    lateinit var mSeekbar: SeekBar
    lateinit var mTvTip: TextView
    override fun onViewCreated(rootView: FrameLayout?) {
        rootView?.let {
            val close = it.findViewById<ImageView>(R.id.iv_close)
            mSeekbar = it.findViewById<SeekBar>(R.id.seekbar)
            mTvTip = it.findViewById<TextView>(R.id.tv_tip)
            mSeekbar.progress = 0
            val tvProgress = it.findViewById<TextView>(R.id.tv_progress)
            tvProgress.text = "当前导航进度: 0%"
            close.setOnClickListener {
                DoKit.removeFloating(this)
            }


            mSeekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean
                ) {
                    if (!GpsMockManager.getInstance().isMocking) {
                        LogHelper.i(TAG, "实时导航功能需要依赖位置模拟功能")
                        return
                    }
                    tvProgress.text = "当前导航进度: $progress%"
                    mAMapNavi?.let { navi ->
                        if (navi.naviPath.coordList.isEmpty()) {
                            return
                        }
                        var index: Int =
                            ceil(navi.naviPath.coordList.size * progress / 100.0).toInt()
                        if (index > navi.naviPath.coordList.size - 1) {
                            index = navi.naviPath.coordList.size - 1
                        }
                        val naviLatLng = navi.naviPath.coordList[index]
                        //LogHelper.i("DoKit", "mock LatLng===>${naviLatLng}")
                        GpsMockManager.getInstance()
                            .mockLocationWithNotify(naviLatLng.latitude, naviLatLng.longitude)

                    }


                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {
                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                }

            })


        }


    }

    override fun initDokitViewLayoutParams(params: DoKitViewLayoutParams?) {
        params?.let {
            it.width = ConvertUtils.dp2px(300.0f)
            it.height = DoKitViewLayoutParams.WRAP_CONTENT
            it.gravity = Gravity.TOP or Gravity.LEFT
            it.x = 200
            it.y = 200
        }
    }


    override fun onResume() {
        super.onResume()
        traversAMapView(activity.window.decorView as ViewGroup)
        if (mapView == null) {
            mSeekbar.visibility = View.GONE
            mTvTip.visibility = View.VISIBLE
            return
        }

        mAMapNavi?.let {
            it.naviPath?.let { path ->
                if (path.coordList.isEmpty()) {
                    mSeekbar.visibility = View.GONE
                    mTvTip.visibility = View.VISIBLE
                } else {
                    mSeekbar.visibility = View.VISIBLE
                    mTvTip.visibility = View.GONE
                }
            }

        }

    }

    private var mapView: com.amap.api.maps.BaseMapView? = null

    private fun traversAMapView(viewGroup: ViewGroup) {
        viewGroup.children.forEach {
            when (it) {
                is com.amap.api.maps.BaseMapView -> {
                    mapView = it
                    return
                }
                is ViewGroup -> traversAMapView(it)
            }
        }
    }

}
