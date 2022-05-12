package com.didichuxing.doraemonkit.kit.test.event.processor

import android.app.Activity
import android.view.View
import com.didichuxing.doraemonkit.kit.core.DoKitManager
import com.didichuxing.doraemonkit.kit.test.event.ControlEvent
import com.didichuxing.doraemonkit.kit.test.event.ViewC12c
import com.didichuxing.doraemonkit.util.*

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
class CustomEventProcessor : AbstractEventProcessor() {


    override fun onSimulationEventAction(activity: Activity, view: View?, viewC12c: ViewC12c, controlEvent: ControlEvent) {
        //执行自定义事件
        DoKitManager.MC_CLIENT_PROCESSOR?.process(
            ActivityUtils.getTopActivity(),
            view,
            viewC12c.actionName,
            viewC12c.params
        )
        onControlEventProcessSuccess(activity, view, controlEvent)
    }

    override fun forceViewCheck(): Boolean {
        return false
    }
}
