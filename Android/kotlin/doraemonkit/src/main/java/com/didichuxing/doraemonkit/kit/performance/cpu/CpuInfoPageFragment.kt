package com.didichuxing.doraemonkit.kit.performance.cpu

import android.os.Bundle
import android.view.View
import com.didichuxing.doraemonkit.R
import com.didichuxing.doraemonkit.config.DokitMemoryConfig
import com.didichuxing.doraemonkit.kit.core.SettingItem
import com.didichuxing.doraemonkit.kit.core.SettingItemAdapter.OnSettingItemClickListener
import com.didichuxing.doraemonkit.kit.core.SettingItemAdapter.OnSettingItemSwitchListener
import com.didichuxing.doraemonkit.kit.performance.AbsPerformanceFragment
import com.didichuxing.doraemonkit.kit.performance.manager.PerformanceDataManager
import com.didichuxing.doraemonkit.kit.performance.manager.datasource.DataSourceFactory

/**
 *
 * Desc:cpu信息界面
 * <p>
 * Date: 2020-06-09
 * Updater:
 * Update Time:
 * Update Comments:
 * @property title Int
 * @property performanceType Int
 * @property itemSwitchListener OnSettingItemSwitchListener
 * @property itemClickListener OnSettingItemClickListener
 *
 * Author: pengyushan
 */
open class CpuInfoPageFragment : AbsPerformanceFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        PerformanceDataManager.instance.init()
    }

    override val title= R.string.dk_frameinfo_cpu

    override val performanceType= DataSourceFactory.TYPE_CPU

    override fun getSettingItems(list: MutableList<SettingItem>): MutableList<SettingItem>? {
        list.add(SettingItem(R.string.dk_cpu_detection_switch, 0,isChecked = DokitMemoryConfig.CPU_STATUS))
        return list
    }

    override val itemSwitchListener = object : OnSettingItemSwitchListener {
            override fun onSettingItemSwitch(view: View, data: SettingItem, on: Boolean) {
                if (on) {
                    startMonitor()
                } else {
                    stopMonitor()
                }
                DokitMemoryConfig.CPU_STATUS = on
            }
        }

    override val itemClickListener= object : OnSettingItemClickListener {
            override fun onSettingItemClick(view: View, data: SettingItem) {
                if (data.desc == R.string.dk_item_cache_log) {
//                    val bundle = Bundle()
//                    bundle.putInt(BundleKey.PERFORMANCE_TYPE, PerformanceFragment.CPU)
//                    showContent(PerformanceFragment::class.java, bundle)
                }
            }
        }

    private fun startMonitor() {
        PerformanceDataManager.instance.startMonitorCPUInfo()
        openChartPage(R.string.dk_frameinfo_cpu, DataSourceFactory.TYPE_CPU)
    }

    private fun stopMonitor() {
        PerformanceDataManager.instance.stopMonitorCPUInfo()
        closeChartPage()
    }

    companion object {
        private const val TAG = "CpuMainPageFragment"
    }
}