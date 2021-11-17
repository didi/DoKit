package com.didichuxing.doraemonkit.amap_api

import android.app.Application
import android.util.Log
import com.amap.api.navi.AMapNavi
import com.didichuxing.doraemonkit.util.VersionUtils


/**
 * ================================================
 * 作    者：jaydroid（王学杰）
 * 版    本：1.0
 * 创建日期：2021/11/17-14:55
 * 描    述：
 * 修订历史：
 * ================================================
 */
object AMapWrap {

    const val TAG = "AMapWrap"

    private val isAMapNaviVersionGreaterV810: Boolean by lazy {
        val aMapNaviVersion = AMapNavi.getVersion()
//        Log.v(TAG, "aMapNaviVersion: $aMapNaviVersion")
        //由于个人信息保护法的实施，从8.1.0版本开始，请务必确保调用SDK任何接口前先调用更新隐私合规updatePrivacyShow、updatePrivacyAgree两个接口
        val compareResult = VersionUtils.compareVersion(aMapNaviVersion, "8.1.0")
//        Log.v(TAG, "compareResult: $compareResult")
        compareResult != -1
    }

    fun createAMapNavi(application: Application): AMapNavi? {
        return if (isAMapNaviVersionGreaterV810) {
            AMapWrapWithPrivacy.createAMapNavi(application)
        } else {
            AMapWrapNoPrivacy.createAMapNavi(application)
        }
    }


}
