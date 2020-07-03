package com.didichuxing.doraemonkit.hook

import android.annotation.SuppressLint
import android.content.Context
import android.os.IBinder
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Proxy

/**
 * Created by jint on 2020/05/29
 */
class ServiceHookManager private constructor() {
    var isHookSuccess = false
        private set
    private val mHookers: MutableList<BaseServiceHooker> = mutableListOf()


    private fun init() {
        mHookers.add(WifiHooker())
        mHookers.add(LocationHooker())
        mHookers.add(TelephonyHooker())
    }

    @SuppressLint("PrivateApi", "DiscouragedPrivateApi")
    fun install(context: Context?) {
        try {
            val serviceManager = Class.forName(CLASS_SERVICE_MANAGER)
            val getService = serviceManager.getDeclaredMethod(METHOD_GET_SERVICE, String::class.java)
            for (hooker in mHookers) {
                val binder = getService.invoke(null, hooker.serviceName()) as IBinder ?: return
                val classLoader = binder.javaClass.classLoader
                val interfaces = arrayOf<Class<*>>(IBinder::class.java)
                val handler = BinderHookHandler(binder, hooker)
                hooker.setBinder(binder)
                val proxy = Proxy.newProxyInstance(classLoader, interfaces, handler) as IBinder
                hooker.replaceBinder(context, proxy)
                val sCache = serviceManager.getDeclaredField(FIELD_S_CACHE)
                sCache.isAccessible = true
                val cache = sCache[null] as MutableMap<String, IBinder>
                hooker.serviceName().let {
                    cache[hooker.serviceName()] = proxy
                    sCache.isAccessible = false
                }
            }
            isHookSuccess = true
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        } catch (e: NoSuchMethodException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        } catch (e: InvocationTargetException) {
            e.printStackTrace()
        } catch (e: NoSuchFieldException) {
            e.printStackTrace()
        }
    }

    companion object {
        private const val CLASS_SERVICE_MANAGER = "android.os.ServiceManager"
        private const val METHOD_GET_SERVICE = "getService"
        private const val FIELD_S_CACHE = "sCache"
        val instance: ServiceHookManager = ServiceHookManager()
    }

    init {
        init()
    }
}