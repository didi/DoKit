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


    override fun serviceName(): String {
        return Context.WIFI_SERVICE
    }

    override fun stubName(): String {
        return "android.net.wifi.IWifiManager\$Stub"
    }

    override fun methodHandlers(): MutableMap<String, MethodHandler?> {
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
        val stub = Class.forName(stubName())
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

        override fun onInvoke(originObject: Any?, proxyObject: Any?, method: Method?, args: Array<Any>?): Any? {
            method?.let { m ->
                try {
                    if (!GpsMockManager.instance.isMocking) {
                        return if (args != null) {
                            m.invoke(originObject, *args)
                        } else {
                            null
                        }
                    }
                } catch (e: IllegalArgumentException) {
                    e.printStackTrace()
                    return null
                }

                try {
                    return Class.forName("android.net.wifi.WifiInfo").newInstance()
                } catch (e: InstantiationException) {
                    e.printStackTrace()
                } catch (e: ClassNotFoundException) {
                    e.printStackTrace()
                }
                return m.invoke(originObject, args)
            }

            return null
        }
    }
}