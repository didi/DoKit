package com.didichuxing.doraemonkit.kit.performance.manager.widget

import androidx.core.util.Pools


/**
 *
 * Desc:折线数据
 * <p>
 * Date: 2020-06-09
 * Copyright: Copyright (c) 2010 - 2020
 * Company: @微微科技有限公司
 * Updater:
 * Update Time:
 * Update Comments:
 * @param value 影响折线幅度的值，必须大于minValue小于maxValue
 * @param label item从右边进入时显示的值，为null的时候不显示
 * @constructor
 *
 * Author: pengyushan
 */
class LineData(var value: Float, var label: String?) {
    fun release() {
        mPool.release(this)
    }

    companion object {
        private val mPool = Pools.SimplePool<LineData>(50)
        @JvmStatic
        fun obtain(value: Float, label: String?): LineData {
            val lineData = mPool.acquire() ?: return LineData(value, label)
            lineData.value = value
            lineData.label = label
            return lineData
        }
    }

}