package com.didichuxing.doraemondemo.dokit

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import com.didichuxing.doraemondemo.R
import com.didichuxing.doraemonkit.kit.core.AbsDokitView
import com.didichuxing.doraemonkit.kit.core.DokitViewLayoutParams
import com.didichuxing.doraemonkit.kit.core.DokitViewManager
import com.didichuxing.doraemonkit.util.LogHelper

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2019-09-23-21:02
 * 描    述：
 * 修订历史：
 * ================================================
 */
class DemoDokitView : AbsDokitView() {
    override fun onCreate(context: Context) {}
    override fun onCreateView(context: Context, rootView: FrameLayout): View {
        return LayoutInflater.from(context).inflate(R.layout.dk_demo, rootView, false)
    }

    override fun onViewCreated(rootView: FrameLayout) {
        val tvClose = findViewById<TextView>(R.id.tv_close)
        tvClose.setOnClickListener { DokitViewManager.getInstance().detach(this@DemoDokitView) }
    }

    override fun initDokitViewLayoutParams(params: DokitViewLayoutParams) {
        params.width = DokitViewLayoutParams.WRAP_CONTENT
        params.height = DokitViewLayoutParams.WRAP_CONTENT
        params.gravity = Gravity.TOP or Gravity.LEFT
        params.x = 200
        params.y = 200
    }

    override fun onPause() {
        super.onPause()
    }




}