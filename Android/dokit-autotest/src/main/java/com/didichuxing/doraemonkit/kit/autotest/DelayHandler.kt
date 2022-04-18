package com.didichuxing.doraemonkit.kit.autotest

import android.os.Handler
import android.os.Looper


/**
 * didi Create on 2022/4/18 .
 *
 * Copyright (c) 2022/4/18 by didiglobal.com.
 *
 * @author <a href="realonlyone@126.com">zhangjun</a>
 * @version 1.0
 * @Date 2022/4/18 3:11 下午
 * @Description 用一句话说明文件功能
 */

class DelayHandler {
    private val mainHandler: Handler = Handler(Looper.getMainLooper())


    fun postDelayed(runnable: Runnable, delay: Long) {
        mainHandler.postDelayed(runnable, delay)
    }

    fun removeCallbacks(runnable: Runnable) {
        mainHandler.removeCallbacks(runnable)
    }

}
