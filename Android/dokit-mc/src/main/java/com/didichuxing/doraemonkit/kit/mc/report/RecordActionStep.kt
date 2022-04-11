package com.didichuxing.doraemonkit.kit.mc.report

import com.didichuxing.doraemonkit.kit.mc.net.WSEvent

data class RecordActionStep(
    val dateTime: String,
    val type: String,
    val event: WSEvent,
    val dataList: MutableList<RecordData>
)
