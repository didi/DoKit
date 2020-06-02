package com.didichuxing.doraemonkit.hook

import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.os.SystemClock
import android.text.TextUtils
import com.didichuxing.doraemonkit.kit.gpsmock.GpsMockManager
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import java.util.*

/**
 * Created by wanglikun on 2019/4/2
 */
class LocationHooker : BaseServiceHooker() {
    //    private List<LocationListener> mListeners = new ArrayList<>();
    //    private LocationListener mHookLocationListener = null;
    override val serviceName: String
        get() = Context.LOCATION_SERVICE

    override val stubName: String
        get() = "android.location.ILocationManager\$Stub"

    //methodHandlers.put("removeUpdates", new RemoveUpdatesMethodHandler());
    override val methodHandlers: MutableMap<String, MethodHandler?>
        get() {
            val methodHandlers: MutableMap<String, MethodHandler?> = mutableMapOf()
            //methodHandlers.put("removeUpdates", new RemoveUpdatesMethodHandler());
            methodHandlers["requestLocationUpdates"] = RequestLocationUpdatesMethodHandler()
            methodHandlers["getLastLocation"] = GetLastLocationMethodHandler()
            methodHandlers["getLastKnownLocation"] = GetLastKnownLocationMethodHandler()
            return methodHandlers
        }

    @Throws(NoSuchFieldException::class, IllegalAccessException::class, ClassNotFoundException::class, NoSuchMethodException::class, InvocationTargetException::class)
    override fun replaceBinder(context: Context?, proxy: IBinder?) {
        val locationManager = context!!.getSystemService(Context.LOCATION_SERVICE) as LocationManager
                ?: return
        val locationManagerClass: Class<*> = locationManager.javaClass
        val mServiceField = locationManagerClass.getDeclaredField("mService")
        mServiceField.isAccessible = true
        val stub = Class.forName(stubName)
        val asInterface = stub.getDeclaredMethod(METHOD_ASINTERFACE, IBinder::class.java)
        mServiceField[locationManager] = asInterface.invoke(null, proxy)
        mServiceField.isAccessible = false
    }

    internal class GetLastKnownLocationMethodHandler : MethodHandler {
        @Throws(InvocationTargetException::class, IllegalAccessException::class, NoSuchMethodException::class)
        override fun onInvoke(originObject: Any?, proxyObject: Any?, method: Method?, args: Array<Any>?): Any? {
            if (!GpsMockManager.instance.isMocking) {
                return method?.invoke(originObject, args)
            }
            var lastKnownLocation: Location? = method?.invoke(originObject, args) as Location
            if (lastKnownLocation == null) {
                val provider = args!![0].javaClass.getDeclaredMethod("getProvider").invoke(args[0]) as String
                lastKnownLocation = buildValidLocation(provider)
            }
            lastKnownLocation.longitude = GpsMockManager.instance.longitude
            lastKnownLocation.latitude = GpsMockManager.instance.latitude
            lastKnownLocation.time = System.currentTimeMillis()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                lastKnownLocation.elapsedRealtimeNanos = SystemClock.elapsedRealtimeNanos()
            }
            return lastKnownLocation
        }
    }

    internal class GetLastLocationMethodHandler : MethodHandler {
        @Throws(InvocationTargetException::class, IllegalAccessException::class)
        override fun onInvoke(originObject: Any?, proxyObject: Any?, method: Method?, args: Array<Any>?): Any? {
            if (!GpsMockManager.instance.isMocking) {
                return method!!.invoke(originObject, args)
            }
            var lastLocation = method!!.invoke(originObject, args) as Location
            if (lastLocation == null) {
                lastLocation = buildValidLocation(null)
            }
            lastLocation.longitude = GpsMockManager.instance.longitude
            lastLocation.latitude = GpsMockManager.instance.latitude
            lastLocation.time = System.currentTimeMillis()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                lastLocation.elapsedRealtimeNanos = SystemClock.elapsedRealtimeNanos()
            }
            return lastLocation
        }
    }

    /**
     * LocationListener代理
     */
    private class LocationListenerProxy(val locationListener: LocationListener?) : LocationListener {
        /**
         * 原始LocationListener
         */

        override fun onLocationChanged(location: Location) {
            if (locationListener != null) {
                if (GpsMockManager.instance.isMocking) {
                    location.longitude = GpsMockManager.instance.longitude
                    location.latitude = GpsMockManager.instance.latitude
                    location.time = System.currentTimeMillis()
                }
                locationListener.onLocationChanged(location)
            }
        }

        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {
            locationListener?.onStatusChanged(provider, status, extras)
        }

        override fun onProviderEnabled(provider: String) {
            locationListener?.onProviderEnabled(provider)
        }

        override fun onProviderDisabled(provider: String) {
            locationListener?.onProviderDisabled(provider)
        }


    }

    /**
     * transport:ListenerTransport 内部包含LocationListener
     */
    internal class RequestLocationUpdatesMethodHandler : MethodHandler {
        /**
         * @param originService 原始对象 LocationManager#mService
         * @param proxy         生成的代理对象
         * @param method        需要被代理的方法 LocationManager#mService.requestLocationUpdates(request, transport, intent, packageName)
         * @param args          代理方法的参数 request, transport, intent, packageName
         * @return
         * @throws IllegalAccessException
         * @throws InvocationTargetException
         * @throws NoSuchFieldException
         */
        @Throws(IllegalAccessException::class, InvocationTargetException::class, NoSuchFieldException::class)
        override fun onInvoke(originService: Any?, proxy: Any?, method: Method?, args: Array<Any>?): Any? {
            if (!GpsMockManager.instance.isMocking) {
                return method!!.invoke(originService, args)
            }
            val listenerTransport = args!![1]
            //LocationListener mListener 类型
            val mListenerField = listenerTransport.javaClass.getDeclaredField("mListener")
            mListenerField.isAccessible = true
            val locationListener = mListenerField[listenerTransport] as LocationListener
            val locationListenerProxy = LocationListenerProxy(locationListener)
            //将原始的LocationListener替换为LocationListenerProxy
            mListenerField[listenerTransport] = locationListenerProxy
            mListenerField.isAccessible = false
            return method!!.invoke(originService, args)
        }
    }

    companion object {
        private const val TAG = "LocationHooker"
        private fun buildValidLocation(provider: String?): Location {
            var provider = provider
            if (TextUtils.isEmpty(provider)) {
                provider = "gps"
            }
            val validLocation = Location(provider)
            validLocation.accuracy = 5.36f
            validLocation.bearing = 315.0f
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                validLocation.bearingAccuracyDegrees = 52.285362f
            }
            validLocation.speed = 0.79f
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                validLocation.speedAccuracyMetersPerSecond = 0.9462558f
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                validLocation.verticalAccuracyMeters = 8.0f
            }
            validLocation.time = System.currentTimeMillis()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                validLocation.elapsedRealtimeNanos = SystemClock.elapsedRealtimeNanos()
            }
            return validLocation
        }
    }
}