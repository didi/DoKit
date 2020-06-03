package com.didichuxing.doraemonkit.aop.method_stack

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020/4/23-17:42
 * 描    述：
 * 修订历史：
 * ================================================
 */
class MethodStackBean {
    var function: String? = null
    var costTime: String? = null
    var children: MutableList<MethodStackBean>? = null

    fun setCostTime(costTime: Int) {
        this.costTime = costTime.toString() + "ms"
    }
}