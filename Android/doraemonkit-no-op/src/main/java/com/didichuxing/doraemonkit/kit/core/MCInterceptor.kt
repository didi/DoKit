package com.didichuxing.doraemonkit.kit.core

import android.view.View
import android.view.accessibility.AccessibilityEvent

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2/18/21-17:06
 * 描    述：一机多控拦截器
 * 修订历史：
 * ================================================
 */
interface MCInterceptor {

    /**
     * 自定义针对当前控件的信息是否进行拦截
     */
    fun onIntercept(view: View, accessibilityEvent: AccessibilityEvent): Boolean


    /**
     * 构建自定义的服务端参数
     */
    fun serverParams(view: View, accessibilityEvent: AccessibilityEvent): Map<String, String>


    /**
     * 客户端处理特殊的控件手势
     * @return true :自定义处理成功
     * @return false :自定义处理失败
     */
    fun clientProcess(view: View, params: Map<String, String>): Boolean

}