package com.didichuxing.doraemonkit.kit.test.mock.data

import com.didichuxing.doraemonkit.BuildConfig
import com.didichuxing.doraemonkit.kit.core.DoKitManager
import com.didichuxing.doraemonkit.util.AppUtils
import com.didichuxing.doraemonkit.util.DeviceUtils
import com.didichuxing.doraemonkit.util.TimeUtils

/**
 * didi Create on 2022/3/10 .
 *
 * Copyright (c) 2022/3/10 by didiglobal.com.
 *
 * @author <a href="realonlyone@126.com">zhangjun</a>
 * @version 1.0
 * @Date 2022/3/10 7:45 下午
 * @Description 用一句话说明文件功能
 */

data class AppInfo(
    val time: String = TimeUtils.getNowString(),
    val phoneMode: String = DeviceUtils.getModel(),
    val systemVersion: String = DeviceUtils.getSDKVersionName(),
    val appName: String = AppUtils.getAppName(),
    val appVersion: String = AppUtils.getAppVersionName(),
    val dokitVersion: String = BuildConfig.DOKIT_VERSION,
    //产品id
    val pId: String? = DoKitManager.PRODUCT_ID
)
