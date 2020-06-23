package com.didichuxing.doraemonkit.kit.filetransfer.action

import com.blankj.utilcode.util.DeviceUtils

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020/6/23-15:26
 * 描    述：
 * 修订历史：
 * ================================================
 */
object IndexAction {
    fun createIndexInfo(): MutableMap<String, Any> {
        return mutableMapOf<String, Any>().apply {
            this["code"] = 200
            this["data"] = "success"
        }
    }

}