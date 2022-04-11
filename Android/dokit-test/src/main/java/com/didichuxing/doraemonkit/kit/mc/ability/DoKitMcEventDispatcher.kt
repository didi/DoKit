package com.didichuxing.doraemonkit.kit.mc.ability


import com.didichuxing.doraemonkit.kit.mc.net.WSEvent

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020/11/13-15:38
 * 描    述：
 * 修订历史：
 * ================================================
 */
object DoKitMcEventDispatcher {


    private val onActionEventListeners: MutableList<OnActionEventListener> = mutableListOf()

    fun send(wsEvent: WSEvent) {
        onActionEventListeners.forEach {
            it.onActionEvent(wsEvent)
        }
    }

    fun addOnActionEventListener(listener: OnActionEventListener) {
        onActionEventListeners.add(listener)
    }


    fun removeOnActionEventListener(listener: OnActionEventListener) {
        onActionEventListeners.remove(listener)
    }

}
