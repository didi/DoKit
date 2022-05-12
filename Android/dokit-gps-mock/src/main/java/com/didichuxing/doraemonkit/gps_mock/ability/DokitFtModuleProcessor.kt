package com.didichuxing.doraemonkit.kit.filemanager.ability

import com.baidu.mapapi.SDKInitializer
import com.didichuxing.doraemonkit.DoKitEnv.app
import com.didichuxing.doraemonkit.config.GpsMockConfig
import com.didichuxing.doraemonkit.gps_mock.gpsmock.GpsMockManager
import com.didichuxing.doraemonkit.gps_mock.gpsmock.ServiceHookManager
import com.didichuxing.doraemonkit.kit.core.DokitAbility
import java.lang.Exception

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2021/6/7-19:50
 * 描    述：
 * 修订历史：
 * ================================================
 */
class DokitGpsMockModuleProcessor : DokitAbility.DokitModuleProcessor {
    override fun values(): Map<String, Any> {
        return mapOf()
    }

    override  fun proceed(actions: Map<String, Any?>?): Map<String, Any> {
        try {
            actions?.let {
                when (actions["action"]) {
                    "init_gps_mock" -> {
                        if (GpsMockConfig.isGPSMockOpen()) {
                            GpsMockManager.getInstance().startMock()
                        }
                        val latLng = GpsMockConfig.getMockLocation()
                        latLng?.let{it2 -> GpsMockManager.getInstance().mockLocationWithNotify(it2.latitude, it2.longitude)}
                        // 在Application里进行初始化,否则在使用百度SDK的接口时,会报so库链接错误.
                        SDKInitializer.initialize(app)
                        //Hook WIFI GPS Telephony系统服务
                        app?.let { it1 -> ServiceHookManager.install(it1.applicationContext) }
                    }
                    else -> {

                    }
                }
            }
        }catch (e:Exception){
            e.printStackTrace()
        }


        return mapOf()
    }
}
