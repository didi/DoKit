package com.didichuxing.doraemonkit.amap_api

import android.app.Application
import com.amap.api.navi.AMapNavi
import com.amap.api.navi.NaviSetting


/**
 * ================================================
 * 作    者：jaydroid（王学杰）
 * 版    本：1.0
 * 创建日期：2021/11/17-14:55
 * 描    述：
 * 修订历史：
 * ================================================
 */
object AMapWrapWithPrivacy {

    fun createAMapNavi(application: Application): AMapNavi? {
        var aMapNavi: AMapNavi? = null
        //确保调用SDK任何接口前先调用更新隐私合规updatePrivacyShow、updatePrivacyAgree两个接口并且参数值都为true，若未正确设置有崩溃风险
        //官方文档：https://lbs.amap.com/api/android-navi-sdk/guide/create-project/configuration-considerations#t3
        NaviSetting.updatePrivacyShow(application, true, true)
        NaviSetting.updatePrivacyAgree(application, true)
        try {
            aMapNavi = AMapNavi.getInstance(application)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return aMapNavi
    }
}
