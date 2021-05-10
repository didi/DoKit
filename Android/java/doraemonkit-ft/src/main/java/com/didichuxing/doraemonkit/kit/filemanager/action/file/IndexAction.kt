package com.didichuxing.doraemonkit.kit.filemanager.action.file

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
    fun indexInfoRes(): MutableMap<String, Any> {
        return mutableMapOf<String, Any>().apply {
            this["code"] = 200
            this["data"] = "请在www.dokit.cn里的控制台中的文件同步助手中使用该功能"
        }
    }

}