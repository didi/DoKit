package com.didichuxing.doraemonkit.kit.mc.net

import com.didichuxing.doraemonkit.kit.test.event.ControlEvent
import com.didichuxing.doraemonkit.kit.test.event.ControlEventManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020/11/18-17:32
 * 描    述：
 * 修订历史：
 * ================================================
 */
object WSEventProcessor {
    suspend fun process(wsEvent: ControlEvent) {
        try {
            withContext(Dispatchers.Main) {
                try {
                    ControlEventManager.getControlEventProcessor().processControlEvent(wsEvent)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
