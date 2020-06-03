package com.didichuxing.doraemonkit.aop

import com.amap.api.location.AMapLocation
import com.amap.api.location.AMapLocationListener
import com.didichuxing.doraemonkit.kit.gpsmock.GpsMockManager

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2019-12-15-16:18
 * 描    述：高德AMapLocationListenerProxy 通过ASM代码动态插入 高德不会跟随系统hook 腾讯和百度会跟随系统的hook
 * 修订历史：
 * ================================================
 */
public class AMapLocationListenerProxy(var aMapLocationListener: AMapLocationListener?) : AMapLocationListener {
    override fun onLocationChanged(mapLocation: AMapLocation) {
        //TODO("功能待实现")
//        if (GpsMockManager.getInstance().isMocking()) {
//            try {
//                mapLocation.latitude = GpsMockManager.getInstance().getLatitude()
//                mapLocation.longitude = GpsMockManager.getInstance().getLongitude()
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//        }
        if (aMapLocationListener != null) {
            aMapLocationListener!!.onLocationChanged(mapLocation)
        }
    }

}