package com.didichuxing.doraemonkit.amap_api

import android.app.Application
import com.amap.api.navi.AMapNavi


/**
 * ================================================
 * 作    者：jaydroid（王学杰）
 * 版    本：1.0
 * 创建日期：2021/11/17-14:55
 * 描    述：
 * 修订历史：
 * ================================================
 */
object AMapWrapNoPrivacy {

    fun createAMapNavi(application: Application): AMapNavi {
        return AMapNavi.getInstance(application)
    }

}
