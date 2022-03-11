package com.didichuxing.doraemonkit.kit.core

import android.app.Activity
import android.view.View
import android.view.accessibility.AccessibilityEvent

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2/18/21-17:06
 * 描    述：一机多控从机处理器
 * 修订历史：
 * ================================================
 */
interface McClientProcessor {

    /**
     * 客户端处理特殊的控件手势
     * @return true :自定义处理成功
     * @return false :自定义处理失败
     */
    fun process(activity: Activity?, view: View?,eventType: String, params: Map<String, String>)

}
