package com.didichuxing.doraemonkit.kit.parameter

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2019-10-11-20:39
 * 描    述：性能监控页面关闭回调接口
 * 修订历史：
 * ================================================
 */
interface PerformanceFragmentCloseListener {
    /**
     * @param performanceTypee
     */
    fun onClose(performanceTypee: Int)
}