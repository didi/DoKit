package com.didichuxing.doraemonkit.kit.test.event

import android.app.Activity
import android.view.View
import com.didichuxing.doraemonkit.kit.test.util.RandomIdentityUtils

/**
 * didi Create on 2022/4/13 .
 *
 * Copyright (c) 2022/4/13 by didiglobal.com.
 *
 * @author <a href="realonlyone@126.com">zhangjun</a>
 * @version 1.0
 * @Date 2022/4/13 3:07 下午
 * @Description 提供测试事件行为的采集与行为模拟执行，相关事项不建议通过其他方式处理。
 */

object ControlEventManager {


    private var currentEventId: String = ""

    private val onControlEventActionListenerSet: MutableSet<OnControlEventActionListener> = mutableSetOf()
    private val onControlEventActionProcessListenerSet: MutableSet<OnControlEventActionProcessListener> = mutableSetOf()

    private val controlEventProcessor: ControlEventProcessor = ControlEventProcessor()

    fun updateEventId(id: String) {
        if (id.isNullOrEmpty()) {
            currentEventId = createNextEventId()
        } else {
            currentEventId = id
        }
    }

    fun getCurrentEventId(): String {
        return currentEventId
    }

    fun createNextEventId(): String {
        return RandomIdentityUtils.createAid()
    }

    /**
     * ControlEvent 事件发生/执行
     * 来自与事件监听
     */
    fun onControlEventAction(activity: Activity?, view: View?, controlEvent: ControlEvent) {
        updateEventId(controlEvent.eventId)
        onControlEventActionListenerSet.forEach {
            it.onControlEventAction(activity, view, controlEvent)
        }
    }

    fun addOnControlEventActionListener(actionListener: OnControlEventActionListener) {
        onControlEventActionListenerSet.add(actionListener)
    }


    fun removeOnControlEventActionListener(actionListener: OnControlEventActionListener) {
        onControlEventActionListenerSet.remove(actionListener)
    }

    /**
     * 从机接收到 ControlEvent 事件
     * 来自主机事件，从机进行分发和处理
     */
    fun onReceiveControlEventAction(controlEvent: ControlEvent) {
        controlEventProcessor.processControlEvent(controlEvent)
    }

    /**
     * 从机执行 ControlEvent 成功
     */
    fun onControlEventProcessSuccess(activity: Activity? = null, view: View? = null, controlEvent: ControlEvent) {
        onControlEventActionProcessListenerSet.forEach {
            it.onControlEventProcessSuccess(activity, view, controlEvent)
        }
    }

    /**
     * 从机执行 ControlEvent 失败
     */
    fun onControlEventProcessFailed(activity: Activity? = null, view: View? = null, controlEvent: ControlEvent, code: Int, message: String) {
        onControlEventActionProcessListenerSet.forEach {
            it.onControlEventProcessFailed(activity, view, controlEvent, code, message)
        }
    }

    fun addOnControlEventActionListener(processListener: OnControlEventActionProcessListener) {
        onControlEventActionProcessListenerSet.add(processListener)
    }

    fun removeOnControlEventActionListener(processListener: OnControlEventActionProcessListener) {
        onControlEventActionProcessListenerSet.remove(processListener)
    }

    fun getControlEventProcessor(): ControlEventProcessor {
        return controlEventProcessor
    }


}
