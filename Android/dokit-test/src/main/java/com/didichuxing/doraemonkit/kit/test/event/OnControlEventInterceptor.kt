package com.didichuxing.doraemonkit.kit.test.event

import android.app.Activity
import android.view.View


/**
 * didi Create on 2022/4/18 .
 *
 * Copyright (c) 2022/4/18 by didiglobal.com.
 *
 * @author <a href="realonlyone@126.com">zhangjun</a>
 * @version 1.0
 * @Date 2022/4/18 2:51 下午
 * @Description 事件拦截器，在事件发送时，不需要的事件可以通过这里进行拦截
 */

interface OnControlEventInterceptor {
    fun onControlEventAction(activity: Activity?, view: View?, controlEvent: ControlEvent): Boolean
}
