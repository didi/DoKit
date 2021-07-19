package com.didichuxing.doraemondemo

import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2021/6/10-11:00
 * 描    述：
 * 修订历史：
 * ================================================
 */
class CarInvocationHandler(val car: ICar) : InvocationHandler {
    override fun invoke(proxy: Any?, method: Method?, args: Array<Any>?): Any? {
//        println("method name===>${method?.name}")
        when (method?.name) {
            "drive" -> {
                println("before drive")
                car.drive()
                println("after drive")
                return null
            }
            "toString" -> {
                return car.toString()
            }
            else -> {
                return null
            }
        }

    }
}