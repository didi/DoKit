package com.didichuxing.doraemonkit.kit.mc.ability

import com.didichuxing.doraemonkit.BuildConfig
import com.didichuxing.doraemonkit.constant.DoKitConstant
import com.didichuxing.doraemonkit.util.AppUtils
import com.didichuxing.doraemonkit.util.DeviceUtils
import com.didichuxing.doraemonkit.util.TimeUtils

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2021/6/22-20:03
 * 描    述：
 * 修订历史：
 * ================================================
 */
data class AppInfo(
    val time: String = TimeUtils.getNowString(),
    val phoneMode: String = DeviceUtils.getModel(),
    val systemVersion: String = DeviceUtils.getSDKVersionName(),
    val appName: String = AppUtils.getAppName(),
    val appVersion: String = AppUtils.getAppVersionName(),
    val dokitVersion: String = BuildConfig.DOKIT_VERSION,
    //产品id
    val pId: String? = DoKitConstant.PRODUCT_ID
)
