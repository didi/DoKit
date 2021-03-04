package com.didichuxing.doraemonkit.kit.mc.server

import com.didichuxing.doraemonkit.constant.WSEType
import com.didichuxing.doraemonkit.kit.mc.all.WSEvent

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

    /**
     * 处理来自从机的消息
     */
    suspend fun process(wsEvent: WSEvent) {
        when (wsEvent.eventType) {
            WSEType.WSE_TEST -> {
                //ToastUtils.showShort(wsEvent.message)
            }

            WSEType.WSE_CONNECTED -> {
                //ToastUtils.showShort(wsEvent.message)
            }

            WSEType.WSE_CLOSE -> {
                //ToastUtils.showShort(wsEvent.message)
            }

            WSEType.ACTIVITY_BACK_PRESSED -> {
                //ToastUtils.showShort(wsEvent.message)
                //ActivityUtils.getTopActivity().onBackPressed()
            }
        }
    }


}