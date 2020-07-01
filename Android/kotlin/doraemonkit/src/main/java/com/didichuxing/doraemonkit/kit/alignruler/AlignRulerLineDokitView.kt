package com.didichuxing.doraemonkit.kit.alignruler

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.ConvertUtils
import com.blankj.utilcode.util.ScreenUtils
import com.didichuxing.doraemonkit.R
import com.didichuxing.doraemonkit.kit.alignruler.AlignRulerMarkerDokitView.OnAlignRulerMarkerPositionChangeListener
import com.didichuxing.doraemonkit.kit.core.AbsDokitView
import com.didichuxing.doraemonkit.kit.core.DokitViewLayoutParams
import com.didichuxing.doraemonkit.kit.core.DokitViewManager

/**
 * @author: xuchun
 * @time: 2020/6/4 - 10:47
 * @desc: 对齐标尺线View
 */
class AlignRulerLineDokitView : AbsDokitView(), OnAlignRulerMarkerPositionChangeListener {
    private var mMarker: AlignRulerMarkerDokitView? = null
    private var mAlignInfoView: AlignLineView? = null
    override fun onCreate(context: Context) {}
    override fun onDestroy() {
        super.onDestroy()
        mMarker?.removePositionChangeListener(this)
    }

    override fun onCreateView(context: Context, rootView: FrameLayout): View {
        return LayoutInflater.from(context).inflate(R.layout.dk_float_align_ruler_line, rootView, false)
    }

    override fun initDokitViewLayoutParams(params: DokitViewLayoutParams?) {
        params?.let {
            it.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
            it.height = DokitViewLayoutParams.MATCH_PARENT
            it.width = DokitViewLayoutParams.MATCH_PARENT
        }

    }

    override fun onViewCreated(view: FrameLayout) {
        postDelayed(100, Runnable {
            mMarker = DokitViewManager.instance.getDokitView(ActivityUtils.getTopActivity(), AlignRulerMarkerDokitView::class.java.simpleName) as AlignRulerMarkerDokitView?
            mMarker?.addPositionChangeListener(this@AlignRulerLineDokitView)
        })
        setDokitViewNotResponseTouchEvent(rootView)
        mAlignInfoView = findViewById(R.id.info_view)
    }

    override fun onPositionChanged(x: Int, y: Int) {
        /**
         * 限制边界
         */
        var positionX = x
        var positionY = y
        if (!isNormalMode) {
            val iconSize = ConvertUtils.dp2px(30f)
            if (positionY <= iconSize) {
                positionY = iconSize
            }
            if (ScreenUtils.isPortrait()) {
                if (positionY >= screenLongSideLength - iconSize) {
                    positionY = screenLongSideLength - iconSize
                }
            } else {
                if (positionY >= screenShortSideLength - iconSize) {
                    positionY = screenShortSideLength - iconSize
                }
            }
            if (positionX <= iconSize) {
                positionX = iconSize
            }
            if (ScreenUtils.isPortrait()) {
                if (positionX >= screenShortSideLength - iconSize) {
                    positionX = screenShortSideLength - iconSize
                }
            } else {
                if (positionX >= screenLongSideLength - iconSize) {
                    positionX = screenLongSideLength - iconSize
                }
            }
        }
        mAlignInfoView?.showInfo(positionX, positionY)
    }

    override fun canDrag(): Boolean {
        return false
    }

    override fun restrictBorderline(): Boolean {
        return true
    }
}