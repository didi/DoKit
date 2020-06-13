package com.didichuxing.doraemonkit.kit.performance.manager.datasource

import com.didichuxing.doraemonkit.kit.performance.manager.widget.LineData
/**
 *
 * Desc:默认的数据工厂实现类
 * <p>
 * Date: 2020-06-09
 * Updater:
 * Update Time:
 * Update Comments:
 *
 * Author: pengyushan
 */
class DefaultDataSource : IDataSource {
    override fun createData(): LineData? {
        val rate = 50.0f
        return LineData.obtain(rate, Math.round(rate).toString() + "")
    }
}