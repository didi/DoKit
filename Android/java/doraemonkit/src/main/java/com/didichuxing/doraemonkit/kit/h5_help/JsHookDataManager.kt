package com.didichuxing.doraemonkit.kit.h5_help

import com.didichuxing.doraemonkit.kit.h5_help.bean.JsRequestBean
import com.didichuxing.doraemonkit.kit.h5_help.bean.StorageBean

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020/8/28-16:08
 * 描    述：
 * 修订历史：
 * ================================================
 */
object JsHookDataManager {
    val jsRequestMap: MutableMap<String?, JsRequestBean> by lazy { mutableMapOf<String?, JsRequestBean>() }

    val jsLocalStorage: MutableList<StorageBean> by lazy { mutableListOf<StorageBean>() }

    val jsSessionStorage: MutableList<StorageBean> by lazy { mutableListOf<StorageBean>() }

}