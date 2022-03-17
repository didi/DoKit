package com.didichuxing.doraemonkit.kit.mc.server

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import com.didichuxing.doraemonkit.kit.core.AbsDokitView
import com.didichuxing.doraemonkit.kit.core.DokitViewLayoutParams
import com.didichuxing.doraemonkit.mc.R
import com.didichuxing.doraemonkit.util.ConvertUtils
import com.didichuxing.doraemonkit.util.ToastUtils

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2021/6/17-14:37
 * 描    述：
 * 修订历史：
 * ================================================
 */
class HostDokitView : AbsDokitView() {
    override fun onCreate(context: Context?) {
    }

    override fun onCreateView(context: Context?, rootView: FrameLayout?): View {
        val view = LayoutInflater.from(context).inflate(R.layout.dk_dokitview_host, rootView, false)
//        view.findViewById<TextView>(R.id.tv_name).setOnClickListener {
//            ToastUtils.showShort("点击主机")
//        }
        return view
    }

    override fun onViewCreated(rootView: FrameLayout?) {
    }

    override fun initDokitViewLayoutParams(params: DokitViewLayoutParams) {
        params.width = ConvertUtils.dp2px(70.0f)
        params.height = ConvertUtils.dp2px(70.0f)
        params.gravity = Gravity.TOP or Gravity.LEFT
        params.x = ConvertUtils.dp2px(280f)
        params.y = ConvertUtils.dp2px(25f)
    }
}
