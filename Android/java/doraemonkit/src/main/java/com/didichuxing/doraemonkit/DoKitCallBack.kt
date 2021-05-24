package com.didichuxing.doraemonkit

import com.didichuxing.doraemonkit.kit.network.bean.NetworkRecord

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2021/5/19-11:00
 * 描    述：
 * 修订历史：
 * ================================================
 */
interface DoKitCallBack {

    fun onCpuCallBack(value: Float, filePath: String) {

    }

    fun onFpsCallBack(value: Float, filePath: String) {

    }

    fun onMemoryCallBack(value: Float, filePath: String) {

    }

    fun onNetworkCallBack(record: NetworkRecord) {

    }
}