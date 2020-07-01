package com.didichuxing.doraemonkit.kit.uiperformance

import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import com.didichuxing.doraemonkit.R
import com.didichuxing.doraemonkit.kit.core.AbsDokitView
import com.didichuxing.doraemonkit.kit.core.DokitViewLayoutParams
import com.didichuxing.doraemonkit.kit.core.DokitViewManager
import com.didichuxing.doraemonkit.kit.uiperformance.UIPerformanceManager.PerformanceDataListener
import com.didichuxing.doraemonkit.model.ViewInfo
import com.didichuxing.doraemonkit.widget.textview.LabelTextView

/**
 *
 * Desc:界面控件绘制信息相关
 * <p>
 * Date: 2020-06-15
 * Company:
 * Updater:
 * Update Time:
 * Update Comments:
 *
 * Author: pengyushan
 */
class UIPerformanceInfoDokitView : AbsDokitView(), PerformanceDataListener {
    private var mClose: ImageView? = null
    private var mMaxLevelText: LabelTextView? = null
    private var mMaxLevelViewIdText: LabelTextView? = null
    private var mTotalTimeText: LabelTextView? = null
    private var mMaxTimeText: LabelTextView? = null
    private var mMaxTimeViewIdText: LabelTextView? = null
    override fun onCreateView(context: Context, view: FrameLayout): View {
        return LayoutInflater.from(view.context).inflate(R.layout.dk_float_ui_performance_info, view, false)
    }

    override fun onViewCreated(view: FrameLayout) {
        mClose = findViewById(R.id.close)
        mClose!!.setOnClickListener {
            DokitViewManager.instance.detach(UIPerformanceDisplayDokitView::class.java.simpleName)
            DokitViewManager.instance.detach(UIPerformanceInfoDokitView::class.java.simpleName)
            UIPerformanceManager.instance.stopTrack()
        }
        mMaxLevelText = findViewById(R.id.max_level)
        mMaxLevelViewIdText = findViewById(R.id.max_level_view_id)
        mTotalTimeText = findViewById(R.id.total_time)
        mMaxTimeText = findViewById(R.id.max_time)
        mMaxTimeViewIdText = findViewById(R.id.max_time_view_id)
    }

    override fun initDokitViewLayoutParams(params: DokitViewLayoutParams?) {
        params!!.y = 60
        params.height = DokitViewLayoutParams.WRAP_CONTENT
        params.width = DokitViewLayoutParams.WRAP_CONTENT
    }

    override fun onCreate(context: Context) {
        UIPerformanceManager.instance.addListener(this@UIPerformanceInfoDokitView)
    }

    override fun onDestroy() {
        super.onDestroy()
        UIPerformanceManager.instance.removeListener(this)
    }

    override fun onRefresh(viewInfos: List<ViewInfo>?) {
        if (viewInfos == null) {
            return
        }
        var maxLevel = 0
        var maxTime = 0f
        var totalTime = 0f
        var maxLevelViewInfo: ViewInfo? = null
        var maxTimeViewInfo: ViewInfo? = null
        for (viewInfo in viewInfos) {
            if (viewInfo.layerNum > maxLevel) {
                maxLevel = viewInfo.layerNum
                maxLevelViewInfo = viewInfo
            }
            if (viewInfo.drawTime > maxTime) {
                maxTime = viewInfo.drawTime
                maxTimeViewInfo = viewInfo
            }
            totalTime += viewInfo.drawTime
        }
        mMaxLevelText!!.setText(maxLevel.toString())
        if (maxLevelViewInfo != null && !TextUtils.isEmpty(maxLevelViewInfo.id)) {
            mMaxLevelViewIdText!!.setText(maxLevelViewInfo.id)
        }
        mMaxTimeText!!.setText(maxTime.toString() + "ms")
        if (maxTimeViewInfo != null && !TextUtils.isEmpty(maxTimeViewInfo.id)) {
            mMaxTimeViewIdText!!.setText(maxTimeViewInfo.id)
        }
        mTotalTimeText!!.setText(totalTime.toString() + "ms")
    }
}