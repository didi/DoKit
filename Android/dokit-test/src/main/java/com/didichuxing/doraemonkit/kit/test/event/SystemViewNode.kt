package com.didichuxing.doraemonkit.kit.test.event

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020/12/15-15:18
 * 描    述：
 * 修订历史：
 * ================================================
 */
data class SystemViewNode(
    val viewClassName: String = "",
    val viewId: String = "-1",
    val childCount: Int = -1,
    /**
     * childIndexOfViewParent = -1  代表是当前控件
     */
    val childIndexOfViewParent: Int = 0,
    /**
     * 是否是特殊控件 RecycleView
     */
    val isSpecialView: Boolean = false,
    /**
     * 当前事件发在特殊控件中的position
     */
    val currentEventPosition: Int = -1,
    /**
     * 是否是事件消费的view
     */
    val isCurrentEventView: Boolean = false
)
