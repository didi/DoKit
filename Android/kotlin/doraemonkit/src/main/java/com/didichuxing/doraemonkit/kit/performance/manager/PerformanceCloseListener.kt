package com.didichuxing.doraemonkit.kit.performance.manager

/**
 *
 * Desc:性能监控关闭回调接口 系统模式下专用
 * <p>
 * Date: 2020-06-09
 * Company:
 * Updater:
 * Update Time:
 * Update Comments:
 *
 * Author: pengyushan
 */
interface PerformanceCloseListener {
    /**
     * @param performanceType
     */
    fun onClose(performanceType: Int)
}