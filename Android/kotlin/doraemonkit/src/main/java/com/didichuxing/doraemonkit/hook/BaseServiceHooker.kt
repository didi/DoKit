package com.didichuxing.doraemonkit.hook

import android.content.Context
import android.os.IBinder
import java.lang.reflect.InvocationHandler
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method

/**
 * Created by wanglikun on 2019/4/2
 */
abstract class BaseServiceHooker : InvocationHandler {
    /**
     * 系统的真实对象
     */
    private var mOriginService: Any? = null
    abstract val serviceName: String?
    abstract val stubName: String?
    abstract val methodHandlers: MutableMap<String, MethodHandler?>

    /**
     * @param proxy  我们自己包装的代理对象
     * @param method 指代的是我们所要调用真实对象的某个方法的Method对象
     * @param args   指代的是调用真实对象某个方法时接受的参数
     * @return
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     * @throws NoSuchFieldException
     * @throws NoSuchMethodException
     */
    @Throws(InvocationTargetException::class, IllegalAccessException::class, NoSuchFieldException::class, NoSuchMethodException::class)
    override fun invoke(proxy: Any, method: Method, args: Array<Any>): Any? {
        if (mOriginService == null) {
            return null
        }
        return if (methodHandlers.containsKey(method.name) && methodHandlers[method.name] != null) {
            methodHandlers[method.name]!!.onInvoke(mOriginService, proxy, method, args)
        } else {
            method.invoke(mOriginService, *args)
        }
    }

    fun setBinder(binder: IBinder?) {
        try {
            val stub = Class.forName(stubName!!)
            val asInterface = stub.getDeclaredMethod(METHOD_ASINTERFACE, IBinder::class.java)
            mOriginService = asInterface.invoke(null, binder)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @Throws(NoSuchFieldException::class, IllegalAccessException::class, ClassNotFoundException::class, NoSuchMethodException::class, InvocationTargetException::class)
    abstract fun replaceBinder(context: Context?, proxy: IBinder?)
    interface MethodHandler {
        /**
         * @param originObject 原始对象
         * @param proxyObject  生成的代理对象
         * @param method       需要被代理的方法
         * @param args         代理方法的参数
         * @return
         * @throws InvocationTargetException
         * @throws IllegalAccessException
         * @throws NoSuchFieldException
         * @throws NoSuchMethodException
         */
        @Throws(InvocationTargetException::class, IllegalAccessException::class, NoSuchFieldException::class, NoSuchMethodException::class)
        fun onInvoke(originObject: Any?, proxyObject: Any?, method: Method?, args: Array<Any>?): Any?
    }

    companion object {
        @JvmStatic
        protected val METHOD_ASINTERFACE = "asInterface"
    }
}