package com.didichuxing.doraemonkit.kit.timecounter

import android.view.View
import com.didichuxing.doraemonkit.R
import com.didichuxing.doraemonkit.kit.core.SettingItem
import com.didichuxing.doraemonkit.kit.core.SettingItemAdapter
import com.didichuxing.doraemonkit.kit.performance.AbsPerformanceFragment

/**
 * 启动耗时fragment
 *
 * @author yfengtech
 *
 * 2020-07-01 15:56:18
 */
class TimeCounterFragment : AbsPerformanceFragment() {

    private val kitInfo = TimeCounterKit()

    override val title = kitInfo.name
    override val performanceType = 0

    override fun getSettingItems(list: MutableList<SettingItem>): MutableList<SettingItem>? = mutableListOf(
            SettingItem(R.string.dk_item_time_counter_switch, 0,
                    canCheck = true,
                    isChecked = TimeCounterManager.isRunning),
            SettingItem(R.string.dk_item_time_goto_list)
    )

    override val itemSwitchListener = object : SettingItemAdapter.OnSettingItemSwitchListener {
        override fun onSettingItemSwitch(view: View, data: SettingItem, on: Boolean) {
            if (on) {
                TimeCounterManager.start()
            } else {
                TimeCounterManager.stop()
            }
        }
    }
    override val itemClickListener = object : SettingItemAdapter.OnSettingItemClickListener {
        override fun onSettingItemClick(view: View, data: SettingItem) {
            if (data.desc == R.string.dk_item_time_goto_list) {
                // 点击查看记录
                showContent(TimeCounterListFragment::class.java)
            }
        }
    }
}