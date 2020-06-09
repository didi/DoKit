package com.didichuxing.doraemonkit.kit.weaknetwork

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.didichuxing.doraemonkit.R
import com.didichuxing.doraemonkit.kit.core.AbsDokitView
import com.didichuxing.doraemonkit.kit.core.DokitViewLayoutParams
import com.didichuxing.doraemonkit.kit.core.DokitViewManager
import com.didichuxing.doraemonkit.util.DokitUtil

class NetWokDokitView : AbsDokitView() {
    var mTvNetWork: TextView? = null
    var mTvTimeOutTime: TextView? = null
    var mTvRequestSpeed: TextView? = null
    var mTvResponseSpeed: TextView? = null
    var mLlTimeWrap: LinearLayout? = null
    var mLlSpeedWrap: LinearLayout? = null
    var mIvClose: ImageView? = null

    override fun onCreate(context: Context?) {}

    override fun onCreateView(context: Context?, rootView: FrameLayout?): View {
        return LayoutInflater.from(context).inflate(R.layout.dk_float_network, rootView, false)
    }

    override fun onViewCreated(rootView: FrameLayout?) {
        rootView?.apply {
            mTvNetWork = findViewById(R.id.tv_net_type)
            mTvTimeOutTime = findViewById(R.id.tv_time)
            mTvRequestSpeed = findViewById(R.id.tv_request_speed)
            mTvResponseSpeed = findViewById(R.id.tv_response_speed)
            mLlTimeWrap = findViewById(R.id.ll_timeout_wrap)
            mLlSpeedWrap = findViewById(R.id.ll_speed_wrap)
            mIvClose = findViewById<ImageView>(R.id.iv_close).apply {
                setOnClickListener {
                    WeakNetworkManager.get().isActive = false
                    DokitViewManager.instance.detach(NetWokDokitView::class.java)
                }
            }
        }
    }

    override fun initDokitViewLayoutParams(params: DokitViewLayoutParams?) {
        params?.apply {
            width = DokitViewLayoutParams.WRAP_CONTENT
            height = DokitViewLayoutParams.WRAP_CONTENT
            gravity = Gravity.TOP or Gravity.LEFT
            x = 100
            y = 100
        }
    }

    override fun onResume() {
        super.onResume()
        if (mTvNetWork == null) return
        try {
            when (WeakNetworkManager.get().type) {
                WeakNetworkManager.TYPE_TIMEOUT -> showTimeOutView()
                WeakNetworkManager.TYPE_SPEED_LIMIT -> showSpeedLimitView()
                else -> showOffNetworkView()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        invalidate()
    }

    private fun showTimeOutView() {
        mTvNetWork?.text = DokitUtil.getString(R.string.dk_weaknet_type_timeout)
        mTvTimeOutTime?.text = WeakNetworkManager.get().timeOutMillis.toString() + " ms"
        mLlTimeWrap?.visibility = View.VISIBLE
        mLlSpeedWrap?.visibility = View.GONE
    }

    private fun showSpeedLimitView() {
        mTvNetWork?.text = DokitUtil.getString(R.string.dk_weaknet_type_speed)
        mTvRequestSpeed?.text = WeakNetworkManager.get().requestSpeed.toString() + " KB/S"
        mTvResponseSpeed?.text = WeakNetworkManager.get().responseSpeed.toString() + " KB/S"
        mLlTimeWrap?.visibility = View.GONE
        mLlSpeedWrap?.visibility = View.VISIBLE
    }

    private fun showOffNetworkView() {
        mTvNetWork?.text = DokitUtil.getString(R.string.dk_weaknet_type_off)
        mLlTimeWrap?.visibility = View.GONE
        mLlSpeedWrap?.visibility = View.GONE
    }

    override fun invalidate() {
        if (isNormalMode) {
            rootView.layoutParams = normalLayoutParams.apply {
                width = WindowManager.LayoutParams.WRAP_CONTENT
                height = WindowManager.LayoutParams.WRAP_CONTENT
            }
        } else {
            mWindowManager.updateViewLayout(rootView, systemLayoutParams.apply {
                width = WindowManager.LayoutParams.WRAP_CONTENT
                height = WindowManager.LayoutParams.WRAP_CONTENT
            })
        }
    }
}