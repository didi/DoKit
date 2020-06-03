package com.didichuxing.doraemonkit.kit.performance;


/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2019-10-11-20:39
 * 描    述：性能监控关闭回调接口 系统模式下专用
 * 修订历史：
 * ================================================
 */
public interface PerformanceCloseListener {

    /**
     * @param performanceType
     */
    void onClose(int performanceType);
}
