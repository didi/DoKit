package com.didichuxing.doraemonkit.kit.performance.widget

import androidx.core.util.Pools

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2019-10-10-16:50
 * 描    述：
 * 修订历史：
 * ================================================
 */
class LineData
/**
 * @param value 影响折线幅度的值，必须大于minValue小于maxValue
 * @param label item从右边进入时显示的值，为null的时候不显示
 */(var value: Float, var label: String?) {
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