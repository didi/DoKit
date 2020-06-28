package com.didichuxing.doraemonkit.kit.performance

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import com.didichuxing.doraemonkit.R
import com.didichuxing.doraemonkit.kit.core.AbsDokitView
import com.didichuxing.doraemonkit.kit.core.DokitViewLayoutParams

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2019-10-11-16:05
 * 描    述：性能监控 帧率、 CPU、RAM、流量监控统一显示的DokitView 关闭按钮 因为系统模式下 设置不响应事件  需要在盖一层用来响应事件
 * 修订历史：
 * ================================================
 */
class PerformanceCloseDokitView : AbsDokitView() {
    var mLlCloseWrap: LinearLayout? = null
    var mWrap0: FrameLayout? = null
    var mWrap1: FrameLayout? = null
    var mWrap2: FrameLayout? = null
    var mWrap3: FrameLayout? = null
    var mIvClose0: ImageView? = null
    var mIvClose1: ImageView? = null
    var mIvClose2: ImageView? = null
    var mIvClose3: ImageView? = null
    var mPerformanceCloseListener: PerformanceCloseListener? = null
    override fun onCreate(context: Context?) {}
    override fun onCreateView(context: Context?, rootView: FrameLayout?): View {
        return LayoutInflater.from(context).inflate(R.layout.dk_performance_close_wrap, rootView, false)
    }

    fun addItem(index: Int, performanceType: Int) {
        if (mLlCloseWrap == null) {
            return
        }
        val closeViewWrap = mLlCloseWrap!!.getChildAt(index) as FrameLayout
        closeViewWrap.visibility = View.VISIBLE
        closeViewWrap.tag = performanceType
    }

    fun removeItem(index: Int) {
        val closeViewWrap = mLlCloseWrap!!.getChildAt(index) as FrameLayout
        closeViewWrap.visibility = View.GONE
        closeViewWrap.tag = -1
    }

    override fun onViewCreated(rootView: FrameLayout?) {
        mLlCloseWrap = findViewById(R.id.ll_close_wrap)
        mWrap0 = findViewById(R.id.fl_wrap0)
        mIvClose0 = findViewById(R.id.iv_close0)
        mWrap0!!.visibility = View.GONE
        mWrap1 = findViewById(R.id.fl_wrap1)
        mIvClose1 = findViewById(R.id.iv_close1)
        mWrap1!!.visibility = View.GONE
        mWrap2 = findViewById(R.id.fl_wrap2)
        mIvClose2 = findViewById(R.id.iv_close2)
        mWrap2!!.visibility = View.GONE
        mWrap3 = findViewById(R.id.fl_wrap3)
        mIvClose3 = findViewById(R.id.iv_close3)
        mWrap3!!.visibility = View.GONE
        mWrap0!!.setOnClickListener { v ->
            v.visibility = View.GONE
            if (mPerformanceCloseListener != null) {
                mPerformanceCloseListener!!.onClose((v.tag as Int))
            }
        }
        mWrap1!!.setOnClickListener { v ->
            v.visibility = View.GONE
            if (mPerformanceCloseListener != null) {
                mPerformanceCloseListener!!.onClose((v.tag as Int))
            }
        }
        mWrap2!!.setOnClickListener { v ->
            v.visibility = View.GONE
            if (mPerformanceCloseListener != null) {
                mPerformanceCloseListener!!.onClose((v.tag as Int))
            }
        }
        mWrap3!!.setOnClickListener { v ->
            v.visibility = View.GONE
            if (mPerformanceCloseListener != null) {
                mPerformanceCloseListener!!.onClose((v.tag as Int))
            }
        }
    }

    override fun initDokitViewLayoutParams(params: DokitViewLayoutParams?) {
        params!!.gravity = Gravity.RIGHT or Gravity.TOP
        params.width = DokitViewLayoutParams.WRAP_CONTENT
        params.height = DokitViewLayoutParams.WRAP_CONTENT
    }

    fun setPerformanceCloseListener(listener: PerformanceCloseListener?) {
        mPerformanceCloseListener = listener
    }

    override fun canDrag(): Boolean {
        return false
    }
}