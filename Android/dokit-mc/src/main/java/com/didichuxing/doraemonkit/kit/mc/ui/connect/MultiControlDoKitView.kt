package com.didichuxing.doraemonkit.kit.mc.ui.connect

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import com.didichuxing.doraemonkit.kit.test.TestMode
import com.didichuxing.doraemonkit.kit.core.AbsDoKitView
import com.didichuxing.doraemonkit.kit.core.DoKitViewLayoutParams
import com.didichuxing.doraemonkit.kit.mc.MultiControlManager
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
class MultiControlDoKitView : AbsDoKitView() {

    companion object {
        private val HOST = R.string.dk_kit_multi_control_host
        private val CLIENT = R.string.dk_kit_multi_control_client
        private val doKitViewSet: MutableSet<MultiControlDoKitView> = mutableSetOf()

        fun updateConnectMode() {
            doKitViewSet.forEach {
                it.updateConnectMode()
            }
        }
    }


    private var textView: TextView? = null

    override fun onCreate(context: Context?) {
        doKitViewSet.add(this)
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
    }

    fun updateConnectMode() {
        textView.let {
            if (MultiControlManager.getMode() == TestMode.HOST) {
                textView?.text = getString(HOST)
            } else {
                textView?.text = getString(CLIENT)
            }
        }
    }

    override fun initDokitViewLayoutParams(params: DoKitViewLayoutParams) {
        params.width = ConvertUtils.dp2px(65.0f)
        params.height = ConvertUtils.dp2px(65.0f)
        params.gravity = Gravity.TOP or Gravity.LEFT
        params.x = ConvertUtils.dp2px(280f)
        params.y = ConvertUtils.dp2px(25f)
    }

    override fun onDestroy() {
        doKitViewSet.remove(this)
        super.onDestroy()
    }

}
