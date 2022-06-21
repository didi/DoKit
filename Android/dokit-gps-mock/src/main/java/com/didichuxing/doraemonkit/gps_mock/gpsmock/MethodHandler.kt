package com.didichuxing.doraemonkit.gps_mock.gpsmock

import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2021/6/15-16:34
 * 描    述：
 * 修订历史：
 * ================================================
 */
abstract class MethodHandler {
    val TAG = this::class.simpleName

    /**
     * @param originObject 原始对象
     * @param method       需要被代理的方法
     * @param args         代理方法的参数
     * @return Any
     */
    @Throws(
        InvocationTargetException::class,
        IllegalAccessException::class,
        NoSuchFieldException::class,
        NoSuchMethodException::class
    )
    abstract fun onInvoke(originObject: Any, method: Method, args: Array<Any>?): Any?
}
