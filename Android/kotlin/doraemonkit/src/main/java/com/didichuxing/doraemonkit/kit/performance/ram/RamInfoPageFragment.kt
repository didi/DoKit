package com.didichuxing.doraemonkit.kit.performance.ram

import android.os.Bundle
import android.view.View
import com.didichuxing.doraemonkit.R
import com.didichuxing.doraemonkit.config.DokitMemoryConfig
import com.didichuxing.doraemonkit.constant.BundleKey
import com.didichuxing.doraemonkit.kit.core.SettingItem
import com.didichuxing.doraemonkit.kit.core.SettingItemAdapter.OnSettingItemClickListener
import com.didichuxing.doraemonkit.kit.core.SettingItemAdapter.OnSettingItemSwitchListener
import com.didichuxing.doraemonkit.kit.performance.AbsPerformanceFragment
import com.didichuxing.doraemonkit.kit.performance.manager.PerformanceDataManager
import com.didichuxing.doraemonkit.kit.performance.manager.datasource.DataSourceFactory

/**
 *
 * Desc:内存界面
 * <p>
 * Date: 2020-06-09
 * Copyright: Copyright (c) 2010 - 2020
 * Company: @微微科技有限公司
 * Updater:
 * Update Time:
 * Update Comments:
 * @property title Int    标题
 * @property performanceType Int 类型
 * @property itemSwitchListener OnSettingItemSwitchListener  开关监听
 * @property itemClickListener OnSettingItemClickListener    事件监听
 *
 * Author: pengyushan
 */
class RamInfoPageFragment : AbsPerformanceFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        PerformanceDataManager.instance.init()
    }

    override val title = R.string.dk_ram_detection_title

    override val performanceType = DataSourceFactory.TYPE_RAM

    override fun getSettingItems(list: MutableList<SettingItem>): MutableList<SettingItem>? {
        list.add(SettingItem(R.string.dk_ram_detection_switch,0, isChecked = DokitMemoryConfig.RAM_STATUS))
        return list
    }


    override val itemSwitchListener = object : OnSettingItemSwitchListener {
            override fun onSettingItemSwitch(view: View, data: SettingItem, on: Boolean) {
                if (on) {
                    startMonitor()
                } else {
                    stopMonitor()
                }
                DokitMemoryConfig.RAM_STATUS = on
            }
        }

    override val itemClickListener = object : OnSettingItemClickListener {
            override fun onSettingItemClick(view: View, data: SettingItem) {
                if (data.desc == R.string.dk_item_cache_log) {
//                    val bundle = Bundle()
//                    bundle.putInt(BundleKey.PERFORMANCE_TYPE, PerformanceFragment.RAM)
//                    showContent(PerformanceFragment::class.java, bundle)
                }
            }
        }

    protected fun startMonitor() {
        PerformanceDataManager.instance.startMonitorMemoryInfo()
        openChartPage(R.string.dk_ram_detection_title, DataSourceFactory.TYPE_RAM)
    }

    protected fun stopMonitor() {
        PerformanceDataManager.instance.stopMonitorMemoryInfo()
        closeChartPage()
    }

    companion object {
        private const val TAG = "RamMainPageFragment"
    }
}