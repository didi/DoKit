package com.didichuxing.doraemonkit.kit.test.event

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020/12/14-11:37
 * 描    述：
 * 修订历史：
 * ================================================
 */
data class AccessibilityEventNode(
    /**
     * View comm & clicked & long clicked & focused Event Property
     */
    val eventType: Int? = -1,
    val className: String? = "",
    val packageName: String? = "",
    val eventTime: Long? = -1,
    /**
     * View  Text Changed Event Property
     */
    //val text: MutableList<CharSequence>?,
    val beforeText: String? = "",
    val fromIndex: Int? = -1,
    val addedCount: Int? = -1,
    val removedCount: Int? = -1,
    /**
     * View text traversed at movement granularity
     */
    val movementGranularity: Int? = -1,
    val toIndex: Int? = -1,
    val action: Int? = -1,
    /**
     * View scrolled
     */
    val maxScrollX: Int? = -1,
    val maxScrollY: Int? = -1,
    val scrollDeltaX: Int? = -1,
    val scrollDeltaY: Int? = -1,
    val scrollX: Int? = -1,
    val scrollY: Int? = -1,
    val scrollable: Boolean? = false,
    /**
     * Window state changed &  Window content changed
     */
    val contentChangeTypes: Int? = -1,
    /**
     *Windows changed
     */
    val windowChanges: Int? = -1
)
