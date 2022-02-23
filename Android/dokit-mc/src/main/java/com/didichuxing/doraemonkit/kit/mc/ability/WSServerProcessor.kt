package com.didichuxing.doraemonkit.kit.mc.ability

import com.didichuxing.doraemonkit.constant.WSEType
import com.didichuxing.doraemonkit.kit.mc.all.DoKitMcManager
import com.didichuxing.doraemonkit.kit.mc.net.WSEvent
import com.didichuxing.doraemonkit.util.LogHelper
import com.didichuxing.doraemonkit.util.ToastUtils
import io.ktor.http.cio.websocket.*

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020/11/18-17:32
 * 描    述：
 * 修订历史：
 * ================================================
 */
object WSServerProcessor {

    const val TAG = "WSServerProcessor"

    /**
     * 处理来自从机的消息
     */
    fun process(wsEvent: WSEvent) {
        when (wsEvent.eventType) {
            WSEType.WSE_TEST -> {
                LogHelper.e(TAG, "处理事件类型 wsEvent=$wsEvent")
            }

            WSEType.WSE_CLOSE -> {

            }

        }
    }


}
