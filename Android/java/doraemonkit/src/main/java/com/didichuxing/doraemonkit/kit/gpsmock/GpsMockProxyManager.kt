package com.didichuxing.doraemonkit.kit.gpsmock

import android.location.Location
import android.location.LocationListener
import com.amap.api.location.AMapLocationListener
import com.amap.api.navi.AMapNaviListener
import com.baidu.location.BDAbstractLocationListener
import com.baidu.location.BDLocationListener
import com.didichuxing.doraemonkit.aop.map.*
import com.didichuxing.doraemonkit.kit.gpsmock.LocationHooker.LocationListenerProxy
import com.tencent.map.geolocation.TencentLocation
import com.tencent.map.geolocation.TencentLocationListener
import java.util.*

/**
 * 三方地图管理类
 */
object GpsMockProxyManager {
    private val mAMapLocationListenerProxies: MutableList<AMapLocationListenerProxy?> = ArrayList()
    private val mAMapLocationChangedListenerProxies: MutableList<AMapLocationChangedListenerProxy?> =
        ArrayList()
    private val mAMapNaviListenerProxies: MutableList<AMapNaviListenerProxy?> = ArrayList()
    private val mBDAbsLocationListenerProxies: MutableList<BDAbsLocationListenerProxy?> =
        ArrayList()
    private val mBDLocationListenerProxies: MutableList<BDLocationListenerProxy?> = ArrayList()
    private val mTencentLocationListenerProxies: MutableList<TencentLocationListenerProxy?> =
        ArrayList()
    private val mLocationListenerProxies: MutableList<LocationListenerProxy> = ArrayList()


    fun addAMapLocationListenerProxy(aMapLocationListenerProxy: AMapLocationListenerProxy) {
        mAMapLocationListenerProxies.add(aMapLocationListenerProxy)
    }

    fun addAMapLocationChangedListenerProxy(aMapLocationChangedListenerProxy: AMapLocationChangedListenerProxy) {
        mAMapLocationChangedListenerProxies.add(aMapLocationChangedListenerProxy)
    }

    fun addAMapNaviListenerProxy(aMapNaviListenerProxy: AMapNaviListenerProxy) {
        mAMapNaviListenerProxies.add(aMapNaviListenerProxy)
    }

    fun addBDAbsLocationListenerProxy(bdAbsLocationListenerProxy: BDAbsLocationListenerProxy) {
        mBDAbsLocationListenerProxies.add(bdAbsLocationListenerProxy)
    }

    fun addBDLocationListenerProxy(bdLocationListenerProxy: BDLocationListenerProxy) {
        mBDLocationListenerProxies.add(bdLocationListenerProxy)
    }

    fun addTencentLocationListenerProxy(tencentLocationListenerProxy: TencentLocationListenerProxy) {
        mTencentLocationListenerProxies.add(tencentLocationListenerProxy)
    }

    fun addLocationListenerProxy(locationListenerProxy: LocationListenerProxy) {
        mLocationListenerProxies.add(locationListenerProxy)
    }

    fun removeAMapLocationListener(listener: AMapLocationListener) {
        val it = mAMapLocationListenerProxies.iterator()
        while (it.hasNext()) {
            val proxy = it.next()
            if (proxy?.aMapLocationListener === listener) {
                it.remove()
            }
        }
    }

    fun removeAMapNaviListener(listener: AMapNaviListener) {
        val it = mAMapNaviListenerProxies.iterator()
        while (it.hasNext()) {
            val proxy = it.next()
            if (proxy?.aMapNaviListener === listener) {
                it.remove()
            }
        }
    }

    fun removeTencentLocationListener(listener: TencentLocationListener) {
        val it = mTencentLocationListenerProxies.iterator()
        while (it.hasNext()) {
            val proxy = it.next()
            if (proxy?.mTencentLocationListener === listener) {
                it.remove()
            }
        }
    }

    fun removeBDLocationListener(listener: BDLocationListener) {
        val it = mBDLocationListenerProxies.iterator()
        while (it.hasNext()) {
            val proxy = it.next()
            if (proxy?.mBdLocationListener === listener) {
                it.remove()
            }
        }
    }

    fun removeBDAbsLocationListener(listener: BDAbstractLocationListener) {
        val it = mBDAbsLocationListenerProxies.iterator()
        while (it.hasNext()) {
            val proxy = it.next()
            if (proxy?.mBdLocationListener === listener) {
                it.remove()
            }
        }
    }

    fun removeLocationListener(listener: LocationListener) {
        val it = mLocationListenerProxies.iterator()
        while (it.hasNext()) {
            val proxy = it.next()
            if (proxy.locationListener === listener) {
                it.remove()
            }
        }
    }

    fun clearProxy() {
        mAMapLocationListenerProxies.clear()
        mBDAbsLocationListenerProxies.clear()
        mBDLocationListenerProxies.clear()
        mTencentLocationListenerProxies.clear()
        mLocationListenerProxies.clear()
    }

    fun mockLocationWithNotify(location: Location?) {
        if (location == null) return
        
        try {
            notifyLocationListenerProxy(location)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        try {
            notifyAMapLocationListenerProxy(location)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        try {
            notifyBDAbsLocationListenerProxy(location)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        try {
            notifyBDLocationListenerProxy(location)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        try {
            notifyTencentLocationListenerProxy(location)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun notifyAMapLocationListenerProxy(location: Location?) {
        if (location != null) {
            //location
            for (aMapLocationListenerProxy in mAMapLocationListenerProxies) {
                aMapLocationListenerProxy?.onLocationChanged(LocationBuilder.toAMapLocation(location))
            }
            //location source
            for (aMapLocationChangedListenerProxy in mAMapLocationChangedListenerProxies) {
                aMapLocationChangedListenerProxy?.onLocationChanged(location)
            }
            if (GpsMockManager.mockAMapNavLocation()) {
                for (aMapNaviListenerProxy in mAMapNaviListenerProxies) {
                    aMapNaviListenerProxy?.onLocationChange(
                        LocationBuilder.toAMapNaviLocation(
                            location
                        )
                    )
                }
            }


        }

    }

    private fun notifyBDAbsLocationListenerProxy(location: Location?) {
        if (location != null) {
            for (bdAbsLocationListenerProxy in mBDAbsLocationListenerProxies) {
                bdAbsLocationListenerProxy?.onReceiveLocation(LocationBuilder.toBdLocation(location))
            }
        }
    }

    private fun notifyBDLocationListenerProxy(location: Location?) {
        if (location != null) {
            for (bdLocationListenerProxy in mBDLocationListenerProxies) {
                bdLocationListenerProxy?.onReceiveLocation(LocationBuilder.toBdLocation(location))
            }
        }
    }

    private fun notifyTencentLocationListenerProxy(location: Location?) {
        if (location != null) {
            for (tencentLocationListenerProxy in mTencentLocationListenerProxies) {
                tencentLocationListenerProxy?.onLocationChanged(
                    LocationBuilder.toTencentLocation(
                        location
                    ), TencentLocation.ERROR_OK, ""
                )
            }
        }
    }

    private fun notifyLocationListenerProxy(location: Location?) {
        if (location != null) {
            for (systemLocationListenerProxy in mLocationListenerProxies) {
                systemLocationListenerProxy.onLocationChanged(location)
            }
        }
    }


}