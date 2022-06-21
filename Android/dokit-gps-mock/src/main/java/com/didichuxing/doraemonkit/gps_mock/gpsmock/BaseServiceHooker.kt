package com.didichuxing.doraemonkit.gps_mock.gpsmock

import android.content.Context
import android.os.IBinder
import android.os.IInterface
import com.didichuxing.doraemonkit.util.ReflectUtils
import java.lang.reflect.InvocationHandler
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method

/**
 * Created by wanglikun on 2019/4/2
 */
abstract class BaseServiceHooker : InvocationHandler {


    var TAG = this.javaClass.simpleName

    private lateinit var mHookMethods: Map<String, MethodHandler>


    fun init() {
        mHookMethods = registerMethodHandlers()
    }

    /**
     * 本地Binder对象(同进程)或远程Binder的代理对象(跨进程)
     */
    var mBinderStubProxy: IInterface? = null

    abstract fun serviceName(): String

    /**
     * 用来初始化mBinderStubProxy
     *
     * @return
     */
    abstract fun stubName(): String

    abstract fun registerMethodHandlers(): Map<String, MethodHandler>

    @Throws(
        NoSuchFieldException::class,
        IllegalAccessException::class,
        ClassNotFoundException::class,
        NoSuchMethodException::class,
        InvocationTargetException::class
    )
    abstract fun replaceBinderProxy(context: Context, proxy: IBinder)

    /**
     * @param proxy  就是代理对象，newProxyInstance方法的返回对象
     * @param method 指代的是我们所要调用真实对象的某个方法的Method对象
     * @param args   指代的是调用真实对象某个方法时接受的参数
     * @return
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     * @throws NoSuchFieldException
     * @throws NoSuchMethodException
     */
    @Throws(
        InvocationTargetException::class,
        IllegalAccessException::class,
        NoSuchFieldException::class,
        NoSuchMethodException::class
    )
    override fun invoke(proxy: Any, method: Method, args: Array<Any>?): Any? {
        if (mBinderStubProxy == null) {
            return null
        } else try {
            //判断要拦截的方法是否实现已注册
            if (mHookMethods.containsKey(method.name) && mHookMethods[method.name] != null) {
                return mHookMethods[method.name]?.onInvoke(mBinderStubProxy!!, method, args)
            } else {
                return if (args == null) {
                    method.invoke(mBinderStubProxy, null)
                } else {
                    method.invoke(mBinderStubProxy, *args)
                }
            }
        } catch (e: Exception) {
            return null
        }
    }

    /**
     * 获得 native Binder  proxy
     *
     * @param binder
     */
    fun asInterface(binder: IBinder?) {
        try {
            //IInterface 包含了IBinder的proxy 并实现相应的接口能力
            mBinderStubProxy =
                ReflectUtils.reflect(stubName()).method("asInterface", binder).get<IInterface>()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


}
