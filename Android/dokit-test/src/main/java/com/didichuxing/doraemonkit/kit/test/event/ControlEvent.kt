package com.didichuxing.doraemonkit.kit.test.event

import com.didichuxing.doraemonkit.kit.test.util.DateTime

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020/11/17-17:11
 * 描    述：事件对象
 * 修订历史：
 * ================================================
 */
data class ControlEvent(
    var eventId: String = "",
    val eventType: EventType,
    val params: Map<String, String>? = null,
    val viewC12c: ViewC12c? = null,
    val dateTime: String = DateTime.nowTime(),
    val diffTIme: Long = 0
)
