package com.didichuxing.doraemonkit.kit.test.report

import com.didichuxing.doraemonkit.kit.test.event.ControlEvent

data class RecordActionStep(
    val dateTime: String,
    val type: String,
    val event: ControlEvent,
    val dataList: MutableList<RecordData>
)
