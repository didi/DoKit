package com.didichuxing.doraemonkit.kit.alignruler

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.blankj.utilcode.util.ActivityUtils
import com.didichuxing.doraemonkit.R
import com.didichuxing.doraemonkit.config.AlignRulerConfig
import com.didichuxing.doraemonkit.kit.alignruler.AlignRulerMarkerDokitView.OnAlignRulerMarkerPositionChangeListener
import com.didichuxing.doraemonkit.kit.core.AbsDokitView
import com.didichuxing.doraemonkit.kit.core.DokitViewLayoutParams
import com.didichuxing.doraemonkit.kit.core.DokitViewManager
import com.didichuxing.doraemonkit.util.UIUtils

/**
 * @author: xuchun
 * @time: 2020/6/4 - 10:47
 * @desc: 对齐标尺线显示坐标信息View
 */
class AlignRulerInfoDokitView : AbsDokitView(), OnAlignRulerMarkerPositionChangeListener {
    private var mAlignHex: TextView? = null
    private var mClose: ImageView? = null
    private var mMarker: AlignRulerMarkerDokitView? = null
    private var mWindowWidth = 0
    private var mWindowHeight = 0
    override fun onCreate(context: Context) {
        mWindowWidth = UIUtils.widthPixels
        mWindowHeight = UIUtils.heightPixels
    }

    override fun onDestroy() {
        super.onDestroy()
        mMarker?.removePositionChangeListener(this)
    }

    override fun onCreateView(context: Context, rootView: FrameLayout): View {
        return LayoutInflater.from(context).inflate(R.layout.dk_float_align_ruler_info, null)
    }

    override fun initDokitViewLayoutParams(params: DokitViewLayoutParams?) {
        params?.let {
            it.width = screenShortSideLength
            it.height = WindowManager.LayoutParams.WRAP_CONTENT
            it.x = 0
            it.y = UIUtils.heightPixels - UIUtils.dp2px(95f)
        }

    }

    override fun onViewCreated(rootView: FrameLayout) {
        postDelayed(100, Runnable {
            mMarker = DokitViewManager.instance.getDokitView(ActivityUtils.getTopActivity(), AlignRulerMarkerDokitView::class.java.simpleName) as AlignRulerMarkerDokitView?
            mMarker?.addPositionChangeListener(this)

        })
        initView()
    }

    private fun initView() {
        rootView.setOnTouchListener { v, event -> mTouchProxy.onTouchEvent(v, event) }
        mAlignHex = findViewById(R.id.align_hex)
        mClose = findViewById(R.id.close)
        mClose?.setOnClickListener {
            AlignRulerConfig.isAlignRulerOpen = false
            DokitViewManager.instance.detach(AlignRulerMarkerDokitView::class.java.simpleName)
            DokitViewManager.instance.detach(AlignRulerLineDokitView::class.java.simpleName)
            DokitViewManager.instance.detach(AlignRulerInfoDokitView::class.java.simpleName)
        }
    }

    override fun onPositionChanged(x: Int, y: Int) {
        val right = mWindowWidth - x
        val bottom = mWindowHeight - y
        mAlignHex?.text = resources!!.getString(R.string.dk_align_info_text, x, right, y, bottom)
    }
}