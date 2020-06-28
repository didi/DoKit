package com.didichuxing.doraemonkit.config

import com.didichuxing.doraemonkit.constant.CachesKey
import com.didichuxing.doraemonkit.constant.SharedPrefsKey
import com.didichuxing.doraemonkit.model.LatLng
import com.didichuxing.doraemonkit.util.CacheUtils
import com.didichuxing.doraemonkit.util.SharedPrefsUtil

/**
 * @author lostjobs created on 2020/6/27
 */
object GpsMockConfig {
    fun isGPSMockOpen() = SharedPrefsUtil.getBoolean(SharedPrefsKey.GPS_MOCK_OPEN, false)
    fun setGPSMockOpen(open: Boolean) = SharedPrefsUtil.putBoolean(SharedPrefsKey.GPS_MOCK_OPEN, open)
    fun getMockLocation(): LatLng? = CacheUtils.readObject(CachesKey.MOCK_LOCATION) as LatLng?
    fun saveMockLocation(latLng: LatLng?) = CacheUtils.saveObject(CachesKey.MOCK_LOCATION, latLng)
}