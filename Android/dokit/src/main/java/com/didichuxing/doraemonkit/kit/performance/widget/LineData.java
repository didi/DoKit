package com.didichuxing.doraemonkit.kit.performance.widget;

import androidx.core.util.Pools;

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2019-10-10-16:50
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class LineData {
    public float value;
    public String label;
    private static Pools.SimplePool<LineData> mPool = new Pools.SimplePool<>(50);


    /**
     * @param value 影响折线幅度的值，必须大于minValue小于maxValue
     * @param label item从右边进入时显示的值，为null的时候不显示
     */
    public LineData(float value, String label) {
        this.value = value;
        this.label = label;
    }

    public static LineData obtain(float value, String label) {
        LineData lineData = mPool.acquire();
        if (lineData == null) {
            return new LineData(value, label);
        }
        lineData.value = value;
        lineData.label = label;
        return lineData;
    }

    public void release() {
        mPool.release(this);
    }

}
