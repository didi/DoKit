package com.didichuxing.doraemonkit.hook

import com.amap.api.location.AMapLocation
import com.blankj.utilcode.util.ReflectUtils
import com.didichuxing.doraemonkit.kit.gpsmock.GpsMockManager
import com.didichuxing.doraemonkit.util.LogHelper
import de.robv.android.xposed.XC_MethodHook

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：3/17/21-17:58
 * 描    述：
 * 修订历史：
 * ================================================
 */
class AMapClientLastLocationHook : XC_MethodHook() {
    override fun afterHookedMethod(param: MethodHookParam?) {
        super.afterHookedMethod(param)
        LogHelper.i("AMapClientLastLocationHook", "===afterHookedMethod===")
        try {
            if (GpsMockManager.getInstance().isMocking) {
                param?.let {
                    if (param.result is AMapLocation) {
                        (param.result as AMapLocation).apply {
                            latitude = GpsMockManager.getInstance().latitude
                            longitude = GpsMockManager.getInstance().longitude
                            //通过反射强制改变p的值 原因:看mapLocation.setErrorCode
                            ReflectUtils.reflect(this).field("p", 0)
                            errorInfo = "success"
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }


    }
}