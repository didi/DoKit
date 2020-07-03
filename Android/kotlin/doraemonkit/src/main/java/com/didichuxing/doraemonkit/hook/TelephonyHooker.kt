package com.didichuxing.doraemonkit.hook

import android.content.Context
import android.os.IBinder
import android.telephony.CellInfo
import android.telephony.gsm.GsmCellLocation
import com.didichuxing.doraemonkit.kit.gpsmock.GpsMockManager
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import java.util.*

/**
 * Created by wanglikun on 2019/4/2
 */
class TelephonyHooker : BaseServiceHooker() {

    override fun serviceName(): String {
        return Context.TELEPHONY_SERVICE
    }

    override fun stubName(): String {
        return "com.android.internal.telephony.ITelephony\$Stub"
    }

    override fun methodHandlers(): MutableMap<String, MethodHandler?> {
        val methodHandlers: MutableMap<String, MethodHandler?> = mutableMapOf()
        methodHandlers["getAllCellInfo"] = GetAllCellInfoMethodHandler()
        methodHandlers["getCellLocation"] = GetCellLocationMethodHandler()
        methodHandlers["listen"] = ListenMethodHandler()
        return methodHandlers
    }

    override fun replaceBinder(context: Context?, proxy: IBinder?) {}
    internal class GetAllCellInfoMethodHandler : MethodHandler {
        @Throws(InvocationTargetException::class, IllegalAccessException::class)
        override fun onInvoke(originObject: Any?, proxyObject: Any?, method: Method?, args: Array<Any>?): Any? {
            return if (!GpsMockManager.instance.isMocking) {
                method!!.invoke(originObject, args)
            } else mutableListOf<CellInfo>()
        }
    }

    internal class GetCellLocationMethodHandler : MethodHandler {
        @Throws(InvocationTargetException::class, IllegalAccessException::class)
        override fun onInvoke(originObject: Any?, proxyObject: Any?, method: Method?, args: Array<Any>?): Any? {
            return if (!GpsMockManager.instance.isMocking) {
                method!!.invoke(originObject, args)
            } else GsmCellLocation()
        }
    }

    internal class ListenMethodHandler : MethodHandler {
        @Throws(InvocationTargetException::class, IllegalAccessException::class)
        override fun onInvoke(originObject: Any?, proxyObject: Any?, method: Method?, args: Array<Any>?): Any? {
            return if (!GpsMockManager.instance.isMocking) {
                method!!.invoke(originObject, args)
            } else null
        }
    }
}