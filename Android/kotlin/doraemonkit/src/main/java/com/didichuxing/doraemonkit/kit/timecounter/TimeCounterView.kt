package com.didichuxing.doraemonkit.kit.timecounter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.didichuxing.doraemonkit.R
import com.didichuxing.doraemonkit.kit.core.AbsDokitView
import com.didichuxing.doraemonkit.kit.core.DokitViewLayoutParams
import com.didichuxing.doraemonkit.kit.timecounter.bean.ActivityTimeCounterRecord
import com.didichuxing.doraemonkit.kit.timecounter.bean.TimeCounterRecord
import com.didichuxing.doraemonkit.util.UIUtils

/**
 * 耗时记录 悬浮view
 *
 * @author yfengtech
 * 2020-07-02 16:00:51
 */
class TimeCounterView : AbsDokitView() {

    private lateinit var tvTitle: TextView
    private lateinit var tvTotalCost: TextView
    private lateinit var tvDetail: TextView

    override fun onCreate(context: Context) {
    }

    override fun onCreateView(context: Context, rootView: FrameLayout): View {
        return LayoutInflater.from(context).inflate(R.layout.dk_float_time_counter, null)
    }

    override fun onViewCreated(rootView: FrameLayout) {
        tvTitle = rootView.findViewById(R.id.tv_title)
        tvTotalCost = rootView.findViewById(R.id.tv_total_cost)
        tvDetail = rootView.findViewById(R.id.tv_detail)

        rootView.findViewById<ImageView>(R.id.close).setOnClickListener {
            TimeCounterManager.stop()
        }

        val appRecord = TimeCounterManager.getAppCounter().getApplicationRecord()
        if (appRecord != null) showInfo(appRecord)
    }

    override fun initDokitViewLayoutParams(params: DokitViewLayoutParams?) {
        params?.apply {
            width = DokitViewLayoutParams.WRAP_CONTENT
            height = DokitViewLayoutParams.WRAP_CONTENT
            x = UIUtils.dp2px(30f)
            y = UIUtils.dp2px(30f)
        }
    }

    fun showInfo(record: TimeCounterRecord) {
        tvTitle.text = record.title()
        setTotalCost(record.totalCost())
        tvDetail.text = record.detail()
    }

    private fun setTotalCost(cost: Long) {
        val totalCost = "Total Cost: ${cost}ms"
        tvTotalCost.text = totalCost
        when {
            cost <= ActivityTimeCounterRecord.ACTIVITY_COST_FAST -> {
                tvTotalCost.setTextColor(context!!.resources.getColor(R.color.dk_color_48BB31))
            }
            cost <= ActivityTimeCounterRecord.ACTIVITY_COST_SLOW -> {
                tvTotalCost.setTextColor(context!!.resources.getColor(R.color.dk_color_FAD337))
            }
            else -> {
                tvTotalCost.setTextColor(context!!.resources.getColor(R.color.dk_color_FF0006))
            }
        }
    }
}