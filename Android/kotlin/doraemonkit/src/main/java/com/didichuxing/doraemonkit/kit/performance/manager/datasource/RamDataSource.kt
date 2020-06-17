package com.didichuxing.doraemonkit.kit.performance.manager.datasource

import com.didichuxing.doraemonkit.kit.performance.manager.PerformanceDataManager
import com.didichuxing.doraemonkit.kit.performance.manager.widget.LineData
import com.didichuxing.doraemonkit.kit.performance.manager.widget.LineData.Companion.obtain
import kotlin.math.roundToInt

/**
 *
 * Desc:内存数据工厂实现类
 * <p>
 * Date: 2020-06-09
 * Updater:
 * Update Time:
 * Update Comments:
 *
 * Author: pengyushan
 */
class RamDataSource : IDataSource {
    private val mMaxRam: Float = (Runtime.getRuntime().maxMemory() * 1.0 / (1024 * 1024)).toFloat()
    override fun createData(): LineData? {
        val info: Float = PerformanceDataManager.instance.lastMemoryInfo
        return obtain(info / mMaxRam * 100, info.roundToInt().toString() + "MB")
    }

}