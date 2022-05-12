package com.didichuxing.doraemonkit.gps_mock.widget;

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import com.didichuxing.doraemonkit.DoKit
import com.didichuxing.doraemonkit.aop.DokitPluginConfig
import com.didichuxing.doraemonkit.gps_mock.R
import com.didichuxing.doraemonkit.gps_mock.gpsmock.GpsMockFragment
import com.didichuxing.doraemonkit.kit.core.AbsDoKitView
import com.didichuxing.doraemonkit.kit.core.DoKitViewLayoutParams
import com.didichuxing.doraemonkit.util.ConvertUtils
import com.didichuxing.doraemonkit.util.DoKitCommUtil
import com.didichuxing.doraemonkit.util.ToastUtils

class RouteMockDokitView : AbsDoKitView() {
    init {
        viewProps.edgePinned = true
    }

    override fun onCreate(context: Context?) {
    }

    override fun onCreateView(context: Context?, rootView: FrameLayout?): View {
        return LayoutInflater.from(context).inflate(R.layout.dk_dokitview_route_mock, rootView, false)
    }

    override fun onViewCreated(rootView: FrameLayout?) {
        doKitView?.setOnClickListener {
            if (!DokitPluginConfig.SWITCH_DOKIT_PLUGIN) {
                ToastUtils.showShort(DoKitCommUtil.getString(com.didichuxing.doraemonkit.R.string.dk_plugin_close_tip))
            }
            if (!DokitPluginConfig.SWITCH_GPS) {
                ToastUtils.showShort(DoKitCommUtil.getString(com.didichuxing.doraemonkit.R.string.dk_plugin_gps_close_tip))
            }
            DoKit.launchFullScreen(GpsMockFragment::class.java, activity, null, true)
        }
    }

    override fun initDokitViewLayoutParams(params: DoKitViewLayoutParams) {
        params.width = ConvertUtils.dp2px(50.0f)
        params.height = ConvertUtils.dp2px(50.0f)
        params.gravity = Gravity.TOP or Gravity.LEFT
        params.x = ConvertUtils.dp2px(280f)
        params.y = ConvertUtils.dp2px(25f)
    }
}
