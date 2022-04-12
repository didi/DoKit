package com.didichuxing.doraemonkit.kit.test.event

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020/12/8-16:13
 * 描    述：View的特征
 * 修订历史：
 * ================================================
 */

data class ViewC12c(

    val actionType: Int = -9999,
    val actionName: String = "",
    val customEventType: String = "",
    val customParams: String = "",
    val viewRootImplIndex: Int = -1,
    val viewPaths: MutableList<SystemViewNode>? = null,
    val accEventInfo: AccessibilityEventNode? = null,
    var text: String? = "",
    val dokitViewPosInfo: DoKitViewNode? = null
)
