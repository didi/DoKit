package com.didichuxing.doraemonkit.hook

import android.location.GpsStatus
import android.util.Log
import de.robv.android.xposed.XC_MethodHook

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：4/25/21-15:59
 * 描    述：
 * 修订历史：
 * ================================================
 */
class LocationManagerGetGpsStatusMethodHook : XC_MethodHook() {
    companion object {
        const val TAG = "LocationManagerGetGps"
    }

    override fun afterHookedMethod(param: MethodHookParam?) {
        param?.let {
            Log.i(TAG, "GetGpsStatus hook ===>${it.result}")
            val gpsStatus = it.result as GpsStatus
            //修改gpsStatus 的具体值
            com.didichuxing.doraemonkit.kit.gpsmock.LocationHooker.mockGpsStatus(gpsStatus)
        }

        super.afterHookedMethod(param)
    }

}