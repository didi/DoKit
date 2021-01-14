package com.didichuxing.doraemonkit.hook

import android.content.Context
import android.net.wifi.ScanResult
import android.net.wifi.WifiManager
import android.os.IBinder
import com.didichuxing.doraemonkit.kit.gpsmock.GpsMockManager
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import java.util.*

/**
 * Created by wanglikun on 2019/4/2
 */
class WifiHooker : BaseServiceHooker() {
    override val serviceName: String
        get() = Context.WIFI_SERVICE

    override val stubName: String
        get() = "android.net.wifi.IWifiManager\$Stub"

    override val methodHandlers: MutableMap<String, MethodHandler?>
        get() {
            val methodHandlers: MutableMap<String, MethodHandler?> = mutableMapOf()
            methodHandlers["getScanResults"] = GetScanResultsMethodHandler()
            methodHandlers["getConnectionInfo"] = GetConnectionInfoMethodHandler()
            return methodHandlers
        }

    @Throws(NoSuchFieldException::class, IllegalAccessException::class, ClassNotFoundException::class, NoSuchMethodException::class, InvocationTargetException::class)
    override fun replaceBinder(context: Context?, proxy: IBinder?) {
        val wifiManager = context!!.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
                ?: return
        val wifiManagerClass: Class<*> = wifiManager.javaClass
        val mServiceField = wifiManagerClass.getDeclaredField("mService")
        mServiceField.isAccessible = true
        val stub = Class.forName(stubName)
        val asInterface = stub.getDeclaredMethod(BaseServiceHooker.METHOD_ASINTERFACE, IBinder::class.java)
        mServiceField[wifiManager] = asInterface.invoke(null, proxy)
        mServiceField.isAccessible = false
    }

    internal class GetScanResultsMethodHandler : MethodHandler {
        @Throws(InvocationTargetException::class, IllegalAccessException::class)
        override fun onInvoke(originService: Any?, proxy: Any?, method: Method?, args: Array<Any>?): Any? {
            return if (!GpsMockManager.instance.isMocking) {
                method!!.invoke(originService, args)
            } else ArrayList<ScanResult>()
        }
    }

    internal class GetConnectionInfoMethodHandler : MethodHandler {
        @Throws(InvocationTargetException::class, IllegalAccessException::class)
        override fun onInvoke(originObject: Any?, proxyObject: Any?, method: Method?, args: Array<Any>?): Any? {
            if (!GpsMockManager.instance.isMocking) {
                return method!!.invoke(originObject, args)
            }
            try {
                return Class.forName("android.net.wifi.WifiInfo").newInstance()
            } catch (e: InstantiationException) {
                e.printStackTrace()
            } catch (e: ClassNotFoundException) {
                e.printStackTrace()
            }
            return method!!.invoke(originObject, args)
        }
    }
}