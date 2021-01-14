package com.didichuxing.doraemonkit.aop

import com.baidu.location.BDLocation
import com.didichuxing.doraemonkit.kit.gpsmock.GpsMockManager

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2019-12-18-20:01
 * 描    述：对百度地图返回的位置信息进行hook
 * 修订历史：
 * ================================================
 */
public object BDLocationUtil {
    fun proxy(bdLocation: BDLocation): BDLocation {
        //TODO("功能待实现")
//        if (GpsMockManager.getInstance().isMocking()) {
//            try {
//                bdLocation.latitude = GpsMockManager.getInstance().getLatitude()
//                bdLocation.longitude = GpsMockManager.getInstance().getLongitude()
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//        }
        return bdLocation
    }
}