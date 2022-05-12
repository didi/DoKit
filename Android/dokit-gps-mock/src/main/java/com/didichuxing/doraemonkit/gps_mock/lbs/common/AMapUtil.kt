package com.didichuxing.doraemonkit.gps_mock.lbs.common

import com.amap.api.maps.model.LatLng
import com.amap.api.navi.model.NaviLatLng
import com.amap.api.navi.model.search.LatLonPoint

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2/25/21-15:48
 * 描    述：
 * 修订历史：
 * ================================================
 */
object AMapUtil {
    /**
     * 把LatLng对象转化为LatLonPoint对象
     */
    fun convertToLatLonPoint(latlng: NaviLatLng): LatLonPoint {
        return LatLonPoint(latlng.latitude, latlng.longitude)
    }

    /**
     * 把LatLonPoint对象转化为LatLon对象
     */
    fun convertToLatLng(latlng: NaviLatLng): LatLng {
        return LatLng(latlng.latitude, latlng.longitude)
    }
}
