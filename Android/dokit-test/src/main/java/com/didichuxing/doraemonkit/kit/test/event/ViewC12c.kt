package com.didichuxing.doraemonkit.kit.test.event

/**
 * didi Create on 2022/4/13 .
 *
 * Copyright (c) 2022/4/13 by didiglobal.com.
 *
 * @author <a href="realonlyone@126.com">zhangjun</a>
 * @version 1.0
 * @Date 2022/4/13 3:07 下午
 * @Description 用一句话说明文件功能
 */
data class ViewC12c(
    val actionType: ActionType = ActionType.UNKNOWN,
    val actionName: String = ActionType.UNKNOWN.getDesc(),
    val params: Map<String, String> = mutableMapOf(),
    val windowIndex: Int = -1,
    val windowNode: WindowNode? = null,
    val viewPath: String = "",
    val viewPaths: MutableList<SystemViewNode>? = null,
    val accEventType: Int = -1,
    val accEventInfo: AccessibilityEventNode? = null,
    var text: String? = "",
    var touchX: Long = -1,
    var touchY: Long = -1,
    var scrollX: Long = -1,
    var scrollY: Long = -1,
    var inputValue: String = "",
    var position: Position? = null,
    val doKitViewNode: DoKitViewNode? = null,
    val doKitViewPanelNode: DoKitViewPanelNode? = null
)
