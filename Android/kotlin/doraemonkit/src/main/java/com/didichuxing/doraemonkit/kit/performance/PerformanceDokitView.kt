package com.didichuxing.doraemonkit.kit.performance

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import com.blankj.utilcode.util.ActivityUtils
import com.didichuxing.doraemonkit.R
import com.didichuxing.doraemonkit.config.DokitMemoryConfig
import com.didichuxing.doraemonkit.kit.core.AbsDokitView
import com.didichuxing.doraemonkit.kit.core.DokitIntent
import com.didichuxing.doraemonkit.kit.core.DokitViewLayoutParams
import com.didichuxing.doraemonkit.kit.core.DokitViewManager
import com.didichuxing.doraemonkit.kit.performance.manager.PerformanceCloseDokitView
import com.didichuxing.doraemonkit.kit.performance.manager.PerformanceCloseListener
import com.didichuxing.doraemonkit.kit.performance.manager.PerformanceDokitViewManager
import com.didichuxing.doraemonkit.kit.performance.manager.PerformanceFragmentCloseListener
import com.didichuxing.doraemonkit.kit.performance.manager.datasource.DataSourceFactory
import com.didichuxing.doraemonkit.kit.performance.manager.datasource.IDataSource
import com.didichuxing.doraemonkit.kit.performance.manager.widget.LineChart

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2019-10-11-16:05
 * 描    述：性能监控 帧率、 CPU、RAM、流量监控统一显示的DokitView
 * 修订历史：
 * ================================================
 */
class PerformanceDokitView : AbsDokitView(), PerformanceCloseListener {
    var mPerformanceCloseDokitView: PerformanceCloseDokitView? = null
    var mPerformanceWrap: LinearLayout? = null
    var mFlWrap0: FrameLayout? = null
    var mFlWrap1: FrameLayout? = null
    var mFlWrap2: FrameLayout? = null
    var mFlWrap3: FrameLayout? = null
    var mLineChart0: LineChart? = null
    var mLineChart1: LineChart? = null
    var mLineChart2: LineChart? = null
    var mLineChart3: LineChart? = null
    var mIvClose0: ImageView? = null
    var mIvClose1: ImageView? = null
    var mIvClose2: ImageView? = null
    var mIvClose3: ImageView? = null
    private var mPerformanceFragmentCloseListener: PerformanceFragmentCloseListener? = null

    /**
     * 添加性能检测页面的浮标关闭监听
     *
     * @param listener
     */
    fun addPerformanceFragmentCloseListener(listener: PerformanceFragmentCloseListener?) {
        mPerformanceFragmentCloseListener = listener
    }

    /**
     * 移除性能检测页面的浮标关闭监听
     *
     * @param listener
     */
    fun removePerformanceFragmentCloseListener(listener: PerformanceFragmentCloseListener) {
        if (mPerformanceFragmentCloseListener != null && mPerformanceFragmentCloseListener === listener) {
            mPerformanceFragmentCloseListener = null
        }
    }

    override fun onCreate(context: Context?) {}
    override fun onCreateView(context: Context?, rootView: FrameLayout?): View {
        return LayoutInflater.from(context).inflate(R.layout.dk_performance_wrap, rootView, false)
    }

    /**
     * 动态添加性能项目
     *
     * @param performanceType
     * @param title
     * @param interval
     */
    fun addItem(performanceType: Int, title: String?, interval: Int) {
        if (mPerformanceWrap == null) {
            return
        }
        var needOperateViewIndex = -1
        for (index in 0 until mPerformanceWrap!!.childCount) {
            if (mPerformanceWrap!!.getChildAt(index).visibility == View.GONE) {
                needOperateViewIndex = index
                break
            }
        }
        if (needOperateViewIndex == -1) {
            return
        }
        val needOperateViewWrap = mPerformanceWrap!!.getChildAt(needOperateViewIndex) as FrameLayout
        needOperateViewWrap.visibility = View.VISIBLE
        val needOperateLineChart: LineChart = needOperateViewWrap.findViewWithTag("lineChart")
        val dataSource: IDataSource = DataSourceFactory.createDataSource(performanceType)
        needOperateLineChart.performanceType = performanceType
        needOperateLineChart.setTitle(title)
        needOperateLineChart.setInterval(interval)
        needOperateLineChart.setDataSource(dataSource)
        needOperateLineChart.startMove()
        //系统模式下添加关闭按钮
        if (!isNormalMode && mPerformanceCloseDokitView != null) {
            mPerformanceCloseDokitView?.addItem(needOperateViewIndex, performanceType)
        }
    }

    fun removeItem(performanceType: Int) {
        if (mPerformanceWrap == null) {
            return
        }
        var needOperateViewIndex = -1
        for (index in 0 until mPerformanceWrap!!.childCount) {
            if (mPerformanceWrap!!.getChildAt(index).visibility != View.GONE) {
                val needOperateLineChart: LineChart = mPerformanceWrap!!.getChildAt(index).findViewWithTag("lineChart")
                if (needOperateLineChart.performanceType == performanceType) {
                    needOperateViewIndex = index
                    break
                }
            }
        }
        if (needOperateViewIndex == -1) {
            return
        }
        val frameLayout = mPerformanceWrap!!.getChildAt(needOperateViewIndex) as FrameLayout
        frameLayout.visibility = View.GONE
        val needOperateLineChart: LineChart = frameLayout.findViewWithTag("lineChart")
        needOperateLineChart.stopMove()
        needOperateLineChart.performanceType = -1
        when (performanceType) {
            DataSourceFactory.TYPE_FPS -> DokitMemoryConfig.FPS_STATUS = false
            DataSourceFactory.TYPE_CPU -> DokitMemoryConfig.CPU_STATUS = false
            DataSourceFactory.TYPE_RAM -> DokitMemoryConfig.RAM_STATUS = false
            DataSourceFactory.TYPE_NETWORK -> DokitMemoryConfig.NETWORK_STATUS = false
            else -> {
            }
        }

        //系统模式下添加关闭按钮
        if (!isNormalMode && mPerformanceCloseDokitView != null) {
            mPerformanceCloseDokitView?.removeItem(needOperateViewIndex)
        }
    }





    override fun onViewCreated(rootView: FrameLayout?) {
        mPerformanceWrap = findViewById(R.id.ll_performance_wrap)
        mFlWrap0 = findViewById(R.id.fl_chart0)
        mFlWrap0!!.visibility = View.GONE
        mFlWrap1 = findViewById(R.id.fl_chart1)
        mFlWrap1!!.visibility = View.GONE
        mFlWrap2 = findViewById(R.id.fl_chart2)
        mFlWrap2!!.visibility = View.GONE
        mFlWrap3 = findViewById(R.id.fl_chart3)
        mFlWrap3!!.visibility = View.GONE
        mLineChart0 = findViewById(R.id.linechart0)
        mLineChart1 = findViewById(R.id.linechart1)
        mLineChart2 = findViewById(R.id.linechart2)
        mLineChart3 = findViewById(R.id.linechart3)
        mIvClose0 = findViewById(R.id.iv_close0)
        mIvClose1 = findViewById(R.id.iv_close1)
        mIvClose2 = findViewById(R.id.iv_close2)
        mIvClose3 = findViewById(R.id.iv_close3)
        setDokitViewNotResponseTouchEvent(rootView)
        setDokitViewNotResponseTouchEvent(mLineChart0)
        setDokitViewNotResponseTouchEvent(mLineChart1)
        setDokitViewNotResponseTouchEvent(mLineChart2)
        setDokitViewNotResponseTouchEvent(mLineChart3)
        if (isNormalMode) {
            mIvClose0!!.visibility = View.VISIBLE
            mIvClose1!!.visibility = View.VISIBLE
            mIvClose2!!.visibility = View.VISIBLE
            mIvClose3!!.visibility = View.VISIBLE
        } else {
            mIvClose0!!.visibility = View.GONE
            mIvClose1!!.visibility = View.GONE
            mIvClose2!!.visibility = View.GONE
            mIvClose3!!.visibility = View.GONE
        }
        mIvClose0!!.setOnClickListener { v ->
            val lineChart: LineChart = (v.parent as FrameLayout).findViewWithTag("lineChart")
            onClose(lineChart.performanceType)
        }
        mIvClose1!!.setOnClickListener { v ->
            val lineChart: LineChart = (v.parent as FrameLayout).findViewWithTag("lineChart")
            onClose(lineChart.performanceType)
        }
        mIvClose2!!.setOnClickListener { v ->
            val lineChart: LineChart = (v.parent as FrameLayout).findViewWithTag("lineChart")
            onClose(lineChart.performanceType)
        }
        mIvClose3!!.setOnClickListener { v ->
            val lineChart: LineChart = (v.parent as FrameLayout).findViewWithTag("lineChart")
            onClose(lineChart.performanceType)
        }
    }

    override fun initDokitViewLayoutParams(params: DokitViewLayoutParams?) {
        params!!.flags = DokitViewLayoutParams.FLAG_NOT_FOCUSABLE_AND_NOT_TOUCHABLE
        params.width = DokitViewLayoutParams.MATCH_PARENT
        params.height = DokitViewLayoutParams.MATCH_PARENT
    }

    override fun canDrag(): Boolean {
        return false
    }

    /**
     * 系统模式下显示单独的关闭按钮
     */
    private fun showSystemPerfoemanceCloseDokitView() {
        val dokitIntent = DokitIntent(PerformanceCloseDokitView::class.java)
        DokitViewManager.instance.attach(dokitIntent)
        mPerformanceCloseDokitView = DokitViewManager.instance.getDokitView(ActivityUtils.getTopActivity(), PerformanceCloseDokitView::class.java.getSimpleName()) as PerformanceCloseDokitView
        if (mPerformanceCloseDokitView != null) {
            mPerformanceCloseDokitView?.setPerformanceCloseListener(this@PerformanceDokitView)
        }
    }

    override fun onResume() {
        super.onResume()
        //系统模式下主动添加关闭按钮
        if (!isNormalMode) {
            showSystemPerfoemanceCloseDokitView()
        }

        //普通模式下自己处理页面切换
        if (isNormalMode) {
            hideAllPerformanceView()
            for (performanceViewInfo in PerformanceDokitViewManager.singleperformanceViewInfos.values) {
                PerformanceDokitViewManager.open(performanceViewInfo.performanceType, performanceViewInfo.title, null)
            }
        }
    }

    override fun onClose(performanceType: Int) {
        if (performanceType == -1) {
            return
        }
        /**
         * 点击关闭按钮 回调switch按钮关闭
         */
        if (mPerformanceFragmentCloseListener != null) {
            mPerformanceFragmentCloseListener!!.onClose(performanceType)
        }
        PerformanceDokitViewManager.close(performanceType, PerformanceDokitViewManager.getTitleByPerformanceType(context, performanceType))
    }

    override fun onEnterForeground() {
        super.onEnterForeground()
        if ((mLineChart0?.parent as FrameLayout).visibility == View.VISIBLE) {
            mLineChart0?.startMove()
        }
        if ((mLineChart1?.parent as FrameLayout).visibility == View.VISIBLE) {
            mLineChart1?.startMove()
        }
        if ((mLineChart2?.parent as FrameLayout).visibility == View.VISIBLE) {
            mLineChart2?.startMove()
        }
        if ((mLineChart3?.parent as FrameLayout).visibility == View.VISIBLE) {
            mLineChart3?.startMove()
        }
    }

    override fun onEnterBackground() {
        super.onEnterBackground()
        mLineChart0?.stopMove()
        mLineChart1?.stopMove()
        mLineChart2?.stopMove()
        mLineChart3?.stopMove()
    }

    override fun onDestroy() {
        super.onDestroy()
        mPerformanceFragmentCloseListener = null
        mLineChart0?.stopMove()
        mLineChart0 = null
        mLineChart1?.stopMove()
        mLineChart1 = null
        mLineChart2?.stopMove()
        mLineChart2 = null
        mLineChart3?.stopMove()
        mLineChart3 = null
    }

    /**
     * 隐藏所有的
     */
    private fun hideAllPerformanceView() {
        if (!isNormalMode) {
            return
        }
        mFlWrap0!!.visibility = View.GONE
        mFlWrap1!!.visibility = View.GONE
        mFlWrap2!!.visibility = View.GONE
        mFlWrap3!!.visibility = View.GONE
    }

    companion object {
        const val DEFAULT_REFRESH_INTERVAL = 1000
    }
}