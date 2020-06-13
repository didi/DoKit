package com.didichuxing.doraemonkit.kit.performance.manager.datasource

import com.didichuxing.doraemonkit.kit.performance.manager.PerformanceDataManager
import com.didichuxing.doraemonkit.kit.performance.manager.widget.LineData
import com.didichuxing.doraemonkit.kit.performance.manager.widget.LineData.Companion.obtain
import kotlin.math.roundToInt

/**
 *
 * Desc:cpu数据工厂实现类
 * <p>
 * Date: 2020-06-09
 * Updater:
 * Update Time:
 * Update Comments:
 *
 * Author: pengyushan
 */
class CpuDataSource : IDataSource {
    override fun createData(): LineData? {
        val rate: Float = PerformanceDataManager.instance.lastCpuRate
        return obtain(rate, rate.roundToInt().toString() + "%")
    }
}