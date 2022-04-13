package com.didichuxing.doraemonkit.kit.connect

import android.text.TextUtils
import com.didichuxing.doraemonkit.util.GsonUtils
import com.didichuxing.doraemonkit.util.SPUtils


/**
 * didi Create on 2022/1/18 .
 *
 * Copyright (c) 2022/1/18 by didiglobal.com.
 *
 * @author <a href="realonlyone@126.com">zhangjun</a>
 * @version 1.0
 * @Date 2022/1/18 8:52 下午
 * @Description 用一句话说明文件功能
 */

object ConnectAddressStore {

    private const val SP_FILE_NAME: String = "dokit_studio_connect_address"
    private const val SP_DATA: String = "data"

    private val data: MutableList<ConnectAddress> = mutableListOf()

    fun loadAddress(): MutableList<ConnectAddress> {
        if (data.isEmpty()) {
            data.addAll(loadAddressAll())
        }
        return data.toMutableList()
    }


    fun saveAddress(connectAddress: ConnectAddress) {
        val list = mutableListOf<ConnectAddress>()
        list.addAll(data)
        list.forEach {
            if (!TextUtils.isEmpty(it.url) && TextUtils.equals(it.url, connectAddress.url)) {
                data.remove(it)
            }
        }
        data.add(connectAddress)
        saveAddressAll(data.toMutableList())
    }

    fun removeAddress(connectAddress: ConnectAddress) {
        data.remove(connectAddress)
        saveAddressAll(data.toMutableList())
    }

    fun saveAddressAll(histories: MutableList<ConnectAddress>) {
        data.clear()
        data.addAll(histories)

        val data2 = GsonUtils.toJson(histories)
        SPUtils.getInstance(SP_FILE_NAME).put(SP_DATA, data2)
    }


    private fun loadAddressAll(): MutableList<ConnectAddress> {
        val data = SPUtils.getInstance(SP_FILE_NAME).getString(SP_DATA, "")
        return if (data.isEmpty()) {
            mutableListOf()
        } else {
            val type = GsonUtils.getListType(ConnectAddress::class.java)
            GsonUtils.fromJson(data, type)
        }
    }

}
