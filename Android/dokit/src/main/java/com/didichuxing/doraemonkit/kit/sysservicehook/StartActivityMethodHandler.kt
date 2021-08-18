package com.didichuxing.doraemonkit.kit.sysservicehook

import com.didichuxing.doraemonkit.kit.gpsmock.MethodHandler
import com.didichuxing.doraemonkit.util.LogHelper
import java.lang.reflect.Method

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2021/6/15-17:21
 * 描    述：
 * 修订历史：
 * ================================================
 */
class StartActivityMethodHandler : MethodHandler() {
    override fun onInvoke(originObject: Any, method: Method, args: Array<Any>?): Any? {
        //LogHelper.i(TAG, "===method===$method  $args")
        return if (args == null) {
            method.invoke(originObject, null)
        } else {
            method.invoke(originObject, *args)
        }
    }
}