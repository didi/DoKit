package com.didichuxing.doraemonkit.aop

import com.blankj.utilcode.util.ReflectUtils
import com.didichuxing.doraemonkit.kit.gpsmock.GpsMockManager
import com.tencent.map.geolocation.TencentLocation
import com.tencent.map.geolocation.TencentLocationListener

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2019-12-15-16:18
 * 描    述：腾讯TencentLocationListenerProxy 通过ASM代码动态插入
 * 修订历史：
 * ================================================
 */
public class TencentLocationListenerProxy(var tencentLocationListener: TencentLocationListener?) : TencentLocationListener {
    override fun onLocationChanged(tencentLocation: TencentLocation, i: Int, s: String) {
        //TODO("功能待实现")
//        if (GpsMockManager.getInstance().isMocking()) {
//            try {
//                //tencentLocation 的 对象类型为TxLocation
//                //LogHelper.i(TAG, "matched==onLocationChanged==" + tencentLocation.toString());
//                //b 为fb类型
//                val b = ReflectUtils.reflect(tencentLocation).field("b").get<Any>()
//                //a 为lat
//                ReflectUtils.reflect(b).field("a", GpsMockManager.getInstance().getLatitude())
//                //b 为lng
//                ReflectUtils.reflect(b).field("b", GpsMockManager.getInstance().getLongitude())
//                //LogHelper.i(TAG, "b===>" + b.toString());
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//        }
        if (tencentLocationListener != null) {
            tencentLocationListener!!.onLocationChanged(tencentLocation, i, s)
        }
    }

    override fun onStatusUpdate(s: String, i: Int, s1: String) {
        if (tencentLocationListener != null) {
            tencentLocationListener!!.onStatusUpdate(s, i, s)
        }
    }

    companion object {
        private const val TAG = "TencentLocationListenerProxy"
    }

}