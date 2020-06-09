package com.didichuxing.doraemonkit.kit.performance.manager

/**
 *
 * Desc:性能监控页面关闭回调接口 kt版本
 * <p>
 * Date: 2020-06-09
 * Company:
 * Updater:
 * Update Time:
 * Update Comments:
 *
 * Author: pengyushan
 */
interface PerformanceFragmentCloseListener {
    /**
     * @param performanceTypee
     */
    fun onClose(performanceTypee: Int)
}