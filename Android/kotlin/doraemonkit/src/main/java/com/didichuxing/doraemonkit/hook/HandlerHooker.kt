package com.didichuxing.doraemonkit.hook

import android.annotation.SuppressLint
import android.app.Application
import android.os.Handler
import com.didichuxing.doraemonkit.reflection.Reflection
import java.lang.reflect.InvocationTargetException

object HandlerHooker {

    //是否已经hook成功
    private var isHookSucceed = false
        private set

    fun doHook(app: Application?) {
        try {
            if (isHookSucceed) {
                return
            }
            //解锁调用系统隐藏api的权限
            Reflection.unseal(app)
            hookInstrumentation()
            isHookSucceed = true
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @SuppressLint("PrivateApi", "DiscouragedPrivateApi")
    @Throws(ClassNotFoundException::class, NoSuchMethodException::class, InvocationTargetException::class, IllegalAccessException::class, NoSuchFieldException::class)
    private fun hookInstrumentation() {
        val activityThreadClass = Class.forName("android.app.ActivityThread")
        val currentActivityThreadMethod = activityThreadClass.getDeclaredMethod("currentActivityThread")
        var acc = currentActivityThreadMethod.isAccessible
        if (!acc) {
            currentActivityThreadMethod.isAccessible = true
        }
        val currentActivityThreadObj = currentActivityThreadMethod.invoke(null)
        if (!acc) {
            currentActivityThreadMethod.isAccessible = acc
        }
        val handlerField = activityThreadClass.getDeclaredField("mH")
        acc = handlerField.isAccessible
        if (!acc) {
            handlerField.isAccessible = true
        }
        val handlerObj = handlerField[currentActivityThreadObj] as Handler
        if (!acc) {
            handlerField.isAccessible = acc
        }
        val handlerCallbackField = Handler::class.java.getDeclaredField("mCallback")
        acc = handlerCallbackField.isAccessible
        if (!acc) {
            handlerCallbackField.isAccessible = true
        }
        handlerCallbackField[handlerObj]?.let {
            val oldCallbackObj = handlerCallbackField[handlerObj] as Handler.Callback
            //自定义handlerCallback
            val proxyMHCallback = ProxyHandlerCallback(oldCallbackObj, handlerObj)
            //将自定义callback注入到activityThread的mH对象中 后期回调会走ProxyHandlerCallback
            handlerCallbackField[handlerObj] = proxyMHCallback
            if (!acc) {
                handlerCallbackField.isAccessible = acc
            }
        }
    }
}