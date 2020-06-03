package com.didichuxing.doraemonkit.kit.gpsmock

import com.didichuxing.doraemonkit.hook.ServiceHookManager

/**
 * Created by wanglikun on 2018/12/18.
 */
class GpsMockManager private constructor() {
    var latitude = -1.0
        private set
    var longitude = -1.0
        private set

    fun startMock() {
        isMocking = true
    }

    fun stopMock() {
        isMocking = false
    }


    fun mockLocation(latitude: Double, longitude: Double) {
        this.latitude = latitude
        this.longitude = longitude
    }

    var isMocking: Boolean = false
        get() = field && longitude != -1.0 && latitude != -1.0

    val isMockEnable: Boolean
        get() = ServiceHookManager.instance.isHookSuccess

    companion object {
        private const val TAG = "GpsMockManager"

        private var isMocking = false
        val instance: GpsMockManager = GpsMockManager()
    }
}