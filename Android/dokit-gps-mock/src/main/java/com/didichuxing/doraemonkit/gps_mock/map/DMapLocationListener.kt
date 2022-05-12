package com.didichuxing.doraemonkit.gps_mock.map

import android.location.Location


/**
 * Created by mmxb on 2021/9/16.
 */
interface DMapLocationListener {
    fun getDMapLocation(): Any
    fun onLocationChange(location: Location?)
}
