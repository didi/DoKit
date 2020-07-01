package com.didichuxing.doraemonkit.kit.alignruler

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import com.didichuxing.doraemonkit.R
import com.didichuxing.doraemonkit.kit.core.AbsDokitView
import com.didichuxing.doraemonkit.kit.core.DokitViewLayoutParams
import com.didichuxing.doraemonkit.util.UIUtils.heightPixels
import com.didichuxing.doraemonkit.util.UIUtils.widthPixels
import java.util.*

/**
 * @author: xuchun
 * @time: 2020/6/4 - 10:47
 * @desc: 对齐标尺标记View
 */
class AlignRulerMarkerDokitView : AbsDokitView() {
    private val mPositionChangeListeners: MutableList<OnAlignRulerMarkerPositionChangeListener> = ArrayList()
    override fun onCreateView(context: Context, rootView: FrameLayout): View {
        return LayoutInflater.from(context).inflate(R.layout.dk_float_align_ruler_marker, null)
    }

    override fun onViewCreated(rootView: FrameLayout) {}
    override fun initDokitViewLayoutParams(params: DokitViewLayoutParams?) {
        params?.let {
            it.height = DokitViewLayoutParams.WRAP_CONTENT
            it.width = DokitViewLayoutParams.WRAP_CONTENT
            it.x = widthPixels / 2
            it.y = heightPixels / 2
        }

    }

    override fun onCreate(context: Context) {}
    override fun onDestroy() {
        super.onDestroy()
        removePositionChangeListeners()
    }

    override fun onMove(x: Int, y: Int, dx: Int, dy: Int) {
        super.onMove(x, y, dx, dy)
        for (listener in mPositionChangeListeners) {
            if (isNormalMode) {
                listener.onPositionChanged(normalLayoutParams.leftMargin + rootView.width / 2, normalLayoutParams.topMargin + rootView.height / 2)
            } else {
                listener.onPositionChanged(systemLayoutParams.x + rootView.width / 2, systemLayoutParams.y + rootView.height / 2)
            }
        }
    }

    override fun updateViewLayout(tag: String, isActivityResume: Boolean) {
        super.updateViewLayout(tag, isActivityResume)
        //更新标尺的位置信息
        for (listener in mPositionChangeListeners) {
            if (isNormalMode) {
                listener.onPositionChanged(normalLayoutParams.leftMargin + rootView.width / 2, normalLayoutParams.topMargin + rootView.height / 2)
            } else {
                listener.onPositionChanged(systemLayoutParams.x + rootView.width / 2, systemLayoutParams.y + rootView.height / 2)
            }
        }
    }

    interface OnAlignRulerMarkerPositionChangeListener {
        fun onPositionChanged(x: Int, y: Int)
    }

    fun addPositionChangeListener(positionChangeListener: OnAlignRulerMarkerPositionChangeListener) {
        mPositionChangeListeners.add(positionChangeListener)
        //更新标尺的位置信息
        for (listener in mPositionChangeListeners) {
            if (isNormalMode) {
                listener.onPositionChanged(normalLayoutParams.leftMargin + rootView.width / 2, normalLayoutParams.topMargin + rootView.height / 2)
            } else {
                listener.onPositionChanged(systemLayoutParams.x + rootView.width / 2, systemLayoutParams.y + rootView.height / 2)
            }
        }
    }

    fun removePositionChangeListener(positionChangeListener: OnAlignRulerMarkerPositionChangeListener) {
        mPositionChangeListeners.remove(positionChangeListener)
    }

    private fun removePositionChangeListeners() {
        mPositionChangeListeners.clear()
    }

    override fun restrictBorderline(): Boolean {
        return false
    }
}