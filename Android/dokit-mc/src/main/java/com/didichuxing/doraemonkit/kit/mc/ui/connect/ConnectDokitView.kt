package com.didichuxing.doraemonkit.kit.mc.ui.connect

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import com.didichuxing.doraemonkit.kit.test.TestMode
import com.didichuxing.doraemonkit.kit.core.AbsDokitView
import com.didichuxing.doraemonkit.kit.core.DokitViewLayoutParams
import com.didichuxing.doraemonkit.kit.mc.DoKitMcManager
import com.didichuxing.doraemonkit.kit.mc.net.DokitMcConnectManager
import com.didichuxing.doraemonkit.kit.mc.ui.McPages
import com.didichuxing.doraemonkit.kit.mc.utils.McPageUtils
import com.didichuxing.doraemonkit.mc.R
import com.didichuxing.doraemonkit.util.ConvertUtils

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2021/6/17-14:37
 * 描    述：
 * 修订历史：
 * ================================================
 */
class ConnectDokitView : AbsDokitView() {

    val HOST = "主机"
    val CLIENT = "从机"

    var textView: TextView? = null

    override fun onCreate(context: Context?) {

    }

    override fun onCreateView(context: Context?, rootView: FrameLayout?): View {
        val view = LayoutInflater.from(context).inflate(R.layout.dk_dokitview_connect, rootView, false)
        return view
    }

    override fun onViewCreated(rootView: FrameLayout?) {

        textView = findViewById<TextView>(R.id.tv_name)
        rootView?.setOnClickListener {
            McPageUtils.startFragment(McPages.CONNECT_HISTORY)
        }
        updateConnectMode()

        DokitMcConnectManager.dokitFloatViews.add(this)

    }

    fun updateConnectMode() {
        textView.let {
            if (DoKitMcManager.CONNECT_MODE == TestMode.HOST) {
                textView?.text = HOST
            } else {
                textView?.text = CLIENT
            }
        }
    }

    override fun initDokitViewLayoutParams(params: DokitViewLayoutParams) {
        params.width = ConvertUtils.dp2px(70.0f)
        params.height = ConvertUtils.dp2px(70.0f)
        params.gravity = Gravity.TOP or Gravity.LEFT
        params.x = ConvertUtils.dp2px(280f)
        params.y = ConvertUtils.dp2px(25f)
    }

    override fun onDestroy() {
        super.onDestroy()
        DokitMcConnectManager.dokitFloatViews.remove(this)
    }

}
