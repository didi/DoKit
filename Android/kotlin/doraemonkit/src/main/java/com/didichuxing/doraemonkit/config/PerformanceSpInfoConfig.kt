package com.didichuxing.doraemonkit.config

import com.didichuxing.doraemonkit.constant.SharedPrefsKey
import com.didichuxing.doraemonkit.kit.largepicture.LargePictureManager
import com.didichuxing.doraemonkit.util.SharedPrefsUtil.getBoolean
import com.didichuxing.doraemonkit.util.SharedPrefsUtil.getFloat
import com.didichuxing.doraemonkit.util.SharedPrefsUtil.putBoolean
import com.didichuxing.doraemonkit.util.SharedPrefsUtil.putFloat

/**
 * @author:  maple
 * @time:  2020/6/8 - 13:57
 * @desc: 将配置信息保存在sp中
 */
object PerformanceSpInfoConfig {

    /**
     * 判断是否开启大图检测
     *
     * @return
     */
    fun isLargeImgOpen(): Boolean {
        return getBoolean(SharedPrefsKey.LARGE_IMG_OPEN, false)
    }

    /**
     * 设置打开或者关闭大图检测
     *
     * @param open
     */
    fun setLargeImgOpen(open: Boolean) {
        putBoolean(SharedPrefsKey.LARGE_IMG_OPEN, open)
    }


    /**
     * 设置大图内存阈值
     *
     * @param threshold
     */
    fun setLargeImgMemoryThreshold(threshold: Float) {
        putFloat(SharedPrefsKey.LARGE_IMG_MEMORY_THRESHOLD, threshold)
        LargePictureManager.getInstance().setMemoryThreshold(threshold.toDouble())
    }


    /**
     * 获得大图内存阈值
     *
     * @param threshold
     */
    fun getLargeImgMemoryThreshold(threshold: Float): Double {
        return getFloat(SharedPrefsKey.LARGE_IMG_MEMORY_THRESHOLD, threshold).toDouble()
    }

    /**
     * 设置大图文件阈值
     *
     * @param threshold
     */
    fun setLargeImgFileThreshold(threshold: Float) {
        putFloat(SharedPrefsKey.LARGE_IMG_FILE_THRESHOLD, threshold)
        LargePictureManager.getInstance().setFileThreshold(threshold.toDouble())
    }

    /**
     * 获得大图文件阈值
     *
     * @param threshold
     */
    fun getLargeImgFileThreshold(threshold: Float): Double {
        return getFloat(SharedPrefsKey.LARGE_IMG_FILE_THRESHOLD, threshold).toDouble()
    }
}