package com.didichuxing.doraemonkit.kit.mc.utils

import android.text.TextUtils
import com.didichuxing.doraemonkit.kit.mc.ui.adapter.McClientHistory
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

object ConnectHistoryUtils {

    const val SP_FILE_NAME: String = "mc_connect_history"
    const val SP_DATA: String = "data"

    val data: MutableList<McClientHistory> = mutableListOf()

    fun loadClientHistory(): MutableList<McClientHistory> {
        if (data.isEmpty()) {
            data.addAll(loadClientHistoryAll())
        }
        return data.toMutableList()
    }


    fun saveClientHistory(clientHistory: McClientHistory) {
        val list = mutableListOf<McClientHistory>()
        list.addAll(data)
        list.forEach {
            if (!TextUtils.isEmpty(it.url) && TextUtils.equals(it.url, clientHistory.url)) {
                data.remove(it)
            }
        }
        data.add(clientHistory)
        saveClientHistoryAll(data.toMutableList())
    }

    fun removeClientHistory(clientHistory: McClientHistory) {
        data.remove(clientHistory)
        saveClientHistoryAll(data.toMutableList())
    }

    fun saveClientHistoryAll(histories: MutableList<McClientHistory>) {
        data.clear()
        data.addAll(histories)

        val data2 = GsonUtils.toJson(histories)
        SPUtils.getInstance(SP_FILE_NAME).put(SP_DATA, data2)
    }


    private fun loadClientHistoryAll(): MutableList<McClientHistory> {
        val data = SPUtils.getInstance(SP_FILE_NAME).getString(SP_DATA, "")
        return if (data.isEmpty()) {
            mutableListOf()
        } else {
            val type = GsonUtils.getListType(McClientHistory::class.java)
            GsonUtils.fromJson(data, type)
        }
    }

}
