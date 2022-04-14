package com.didichuxing.doraemonkit.kit.test.event

import android.app.Activity
import android.view.View


/**
 * didi Create on 2022/4/11 .
 *
 * Copyright (c) 2022/4/11 by didiglobal.com.
 *
 * @author <a href="realonlyone@126.com">zhangjun</a>
 * @version 1.0
 * @Date 2022/4/11 3:01 下午
 * @Description 用一句话说明文件功能
 */

interface OnControlEventActionListener {

    fun onControlEventAction(activity: Activity?, view: View?, event: ControlEvent)
}
