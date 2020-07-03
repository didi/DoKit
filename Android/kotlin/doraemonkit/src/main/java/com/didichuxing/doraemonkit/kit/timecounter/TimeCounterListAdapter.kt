package com.didichuxing.doraemonkit.kit.timecounter

import android.content.Context
import android.content.Intent
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.didichuxing.doraemonkit.R
import com.didichuxing.doraemonkit.constant.BundleKey
import com.didichuxing.doraemonkit.constant.FragmentIndex
import com.didichuxing.doraemonkit.kit.core.UniversalActivity
import com.didichuxing.doraemonkit.kit.timecounter.bean.ActivityTimeCounterRecord
import com.didichuxing.doraemonkit.kit.timecounter.bean.ApplicationTimeCounterRecord
import com.didichuxing.doraemonkit.kit.timecounter.bean.TimeCounterRecord
import com.didichuxing.doraemonkit.widget.recyclerview.AbsRecyclerAdapter
import com.didichuxing.doraemonkit.widget.recyclerview.AbsViewBinder

/**
 * 启动耗时 记录列表
 *
 * @author yfengtech
 *
 * 2020-07-02 13:56:45
 */
internal class TimeCounterListAdapter(context: Context?) :
        AbsRecyclerAdapter<TimeCounterListAdapter.TimeCounterItemViewHolder, TimeCounterRecord>(context) {

    /**
     * 记录item是否展开的map
     */
    private val expandedMap: HashMap<Int, Boolean> = HashMap()

    override fun createViewHolder(view: View, viewType: Int): TimeCounterItemViewHolder {
        return TimeCounterItemViewHolder(view)
    }

    override fun createView(inflater: LayoutInflater, parent: ViewGroup?, viewType: Int): View {
        return inflater.inflate(R.layout.dk_item_time_counter_list, parent, false)
    }


    inner class TimeCounterItemViewHolder(view: View) : AbsViewBinder<TimeCounterRecord>(view) {
        private var tvTime: TextView = view.findViewById(R.id.time)
        private var tvTitle: TextView = view.findViewById(R.id.title)
        private var tvTotal: TextView = view.findViewById(R.id.total_cost)
        private var tvDetail: TextView = view.findViewById(R.id.tv_detail)

        override fun onBind(data: TimeCounterRecord, position: Int) {
            val isExpanded = expandedMap[position] ?: false
            showInfo(data, isExpanded)
            itemView.setOnClickListener {
                when (data) {
                    is ActivityTimeCounterRecord -> {
                        val expanded = expandedMap[position] ?: false
                        expandedMap[position] = !expanded
                        showInfo(data, !expanded)
                    }
                    is ApplicationTimeCounterRecord -> {
                        //跳转启动耗时详情页
                        toAppCostDetail()
                    }
                }
            }
        }

        private fun toAppCostDetail() {
            val intent = Intent(itemView.context, UniversalActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            intent.putExtra(BundleKey.FRAGMENT_INDEX, FragmentIndex.FRAGMENT_APP_RECORD_DETAIL)
            itemView.context.startActivity(intent)
        }

        private fun showInfo(data: TimeCounterRecord, isExpanded: Boolean) {

            tvTitle.text = data.title()
            val time = DateUtils.formatDateTime(context, data.recordTime(), DateUtils.FORMAT_SHOW_TIME)
            tvTime.text = time

            val totalCost = data.totalCost()
            setTotalCost(totalCost)

            if (isExpanded) {
                tvDetail.visibility = View.VISIBLE
                tvDetail.text = data.detail()
            } else {
                tvDetail.visibility = View.GONE
            }
        }

        private fun setTotalCost(cost: Long) {
            tvTotal.text = "Total Cost: " + cost + "ms"
            when {
                cost <= ActivityTimeCounterRecord.ACTIVITY_COST_FAST -> {
                    tvTotal.setTextColor(context.resources.getColor(R.color.dk_color_48BB31))
                }
                cost <= ActivityTimeCounterRecord.ACTIVITY_COST_SLOW -> {
                    tvTotal.setTextColor(context.resources.getColor(R.color.dk_color_FAD337))
                }
                else -> {
                    tvTotal.setTextColor(context.resources.getColor(R.color.dk_color_FF0006))
                }
            }
        }
    }
}