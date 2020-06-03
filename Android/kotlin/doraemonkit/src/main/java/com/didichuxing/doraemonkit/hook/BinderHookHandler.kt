package com.didichuxing.doraemonkit.hook

import android.annotation.SuppressLint
import android.os.IBinder
import android.os.IInterface
import java.lang.reflect.InvocationHandler
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import java.lang.reflect.Proxy

/**
 * Created by wanglikun on 2018/12/18.
 */
class BinderHookHandler(private val mOriginService: IBinder, private val mHooker: BaseServiceHooker) : InvocationHandler {
    @SuppressLint("PrivateApi")
    @Throws(InvocationTargetException::class, IllegalAccessException::class)
    override fun invoke(proxy: Any, method: Method, args: Array<Any>): Any {
        return when (method.name) {
            "queryLocalInterface" -> {
                val iManager: Class<*>
                iManager = try {
                    Class.forName(args[0].toString())
                } catch (e: ClassNotFoundException) {
                    e.printStackTrace()
                    return method.invoke(mOriginService, *args)!!
                }
                val classLoader = mOriginService.javaClass.classLoader
                val interfaces = arrayOf(IInterface::class.java, IBinder::class.java, iManager)
                Proxy.newProxyInstance(classLoader, interfaces, mHooker)
            }
            else -> method.invoke(mOriginService, *args)!!
        }
    }

}