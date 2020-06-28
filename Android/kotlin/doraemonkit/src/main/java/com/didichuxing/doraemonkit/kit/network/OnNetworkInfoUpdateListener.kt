package com.didichuxing.doraemonkit.kit.network

import com.didichuxing.doraemonkit.kit.network.okhttp.bean.NetworkRecord

interface OnNetworkInfoUpdateListener {
    /**
     * 网络请求更新时候的回调
     * @param record
     * @param add true表示添加，false表示更新
     */
    fun onNetworkInfoUpdate(record: NetworkRecord?, add: Boolean)
}