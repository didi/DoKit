package com.didichuxing.doraemonkit.kit.performance.manager.datasource

import com.didichuxing.doraemonkit.kit.performance.manager.PerformanceDataManager
import com.didichuxing.doraemonkit.kit.performance.manager.widget.LineData
import com.didichuxing.doraemonkit.kit.performance.manager.widget.LineData.Companion.obtain
import kotlin.math.roundToInt

class FpsDataSource : IDataSource {
    override fun createData(): LineData? {
        val rate: Float = PerformanceDataManager.instance.lastFrameRate
        return obtain(rate, rate.roundToInt().toString() + "")
    }
}