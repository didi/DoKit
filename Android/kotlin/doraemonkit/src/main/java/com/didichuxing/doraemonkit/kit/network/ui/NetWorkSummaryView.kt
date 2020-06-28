package com.didichuxing.doraemonkit.kit.network.ui

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.didichuxing.doraemonkit.R
import com.didichuxing.doraemonkit.kit.network.NetworkManager
import com.didichuxing.doraemonkit.kit.network.utils.ByteUtil
import com.didichuxing.doraemonkit.kit.network.utils.CostTimeUtil
import com.didichuxing.doraemonkit.widget.chart.BarChart
import com.didichuxing.doraemonkit.widget.chart.PieChart
import com.didichuxing.doraemonkit.widget.chart.PieChart.PieData
import java.util.*

class NetWorkSummaryView : LinearLayout {
    constructor(context: Context?) : super(context) {
        View.inflate(context, R.layout.dk_fragment_network_summary_page, this)
        initView()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        View.inflate(context, R.layout.dk_fragment_network_summary_page, this)
        initView()
    }

    private fun initView() {
        val totalSec = findViewById<TextView>(R.id.total_sec)
        val totalNumber = findViewById<TextView>(R.id.total_number)
        val totalUpload = findViewById<TextView>(R.id.total_upload)
        val totalDown = findViewById<TextView>(R.id.total_down)
        val postCount = NetworkManager.instance.postCount
        val getCount = NetworkManager.instance.getCount
        val totalCount = NetworkManager.instance.totalCount
        totalNumber.text = totalCount.toString()
        val time = NetworkManager.instance.runningTime
        totalSec.text = CostTimeUtil.formatTime(context, time)
        val requestSize = NetworkManager.instance.totalRequestSize
        val responseSize = NetworkManager.instance.totalResponseSize
        totalUpload.text = ByteUtil.getPrintSizeForSpannable(requestSize)
        totalDown.text = ByteUtil.getPrintSizeForSpannable(responseSize)
        val chart: PieChart = findViewById(R.id.network_pier_chart)
        val data: MutableList<PieData> = ArrayList()
        val resource = resources
        if (postCount != 0) {
            data.add(PieData(resource.getColor(R.color.dk_color_55A8FD), postCount.toLong()))
        }
        if (getCount != 0) {
            data.add(PieData(resource.getColor(R.color.dk_color_FAD337), getCount.toLong()))
        }
        chart.setData(data)
        val barChart: BarChart = findViewById(R.id.network_bar_chart)
        barChart.setData(postCount, resources.getColor(R.color.dk_color_55A8FD), getCount, resources.getColor(R.color.dk_color_FAD337))
    }
}