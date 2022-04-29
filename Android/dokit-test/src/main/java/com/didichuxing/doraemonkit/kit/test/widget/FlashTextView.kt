package com.didichuxing.doraemonkit.kit.test.widget

import android.content.Context
import android.util.AttributeSet
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn


/**
 * didi Create on 2022/4/14 .
 *
 * Copyright (c) 2022/4/14 by didiglobal.com.
 *
 * @author <a href="realonlyone@126.com">zhangjun</a>
 * @version 1.0
 * @Date 2022/4/14 4:27 下午
 * @Description 用一句话说明文件功能
 */

class FlashTextView : androidx.appcompat.widget.AppCompatTextView {

    private val flashViewScope = MainScope() + CoroutineName(this.toString())

    private var flashFlow = flow {
        while (true) {
            (0..3).forEach {
                emit(it)
                delay(500)
            }
        }
    }

    private var flashEnable: Boolean = false

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    fun isFlashEnable(): Boolean {
        return flashEnable
    }

    fun cancelFlash() {
        flashViewScope.cancel()
        flashEnable = false
    }

    fun startFlash() {
        if (!flashEnable) {
            flashEnable = true
            flashViewScope.launch {
                flashFlow.flowOn(Dispatchers.IO)
                    .collect {
                        when (it) {
                            0 -> text = ""
                            1 -> text = "."
                            2 -> text = ".."
                            3 -> text = "..."
                        }
                    }
            }
        }
    }


}
