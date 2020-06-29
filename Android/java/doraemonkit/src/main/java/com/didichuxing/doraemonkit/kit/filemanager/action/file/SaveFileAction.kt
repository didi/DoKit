package com.didichuxing.doraemonkit.kit.filemanager.action.file

import com.blankj.utilcode.util.FileIOUtils
import com.blankj.utilcode.util.FileUtils

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020/6/23-15:26
 * 描    述：
 * 修订历史：
 * ================================================
 */
object SaveFileAction {
    fun saveFileRes(content: String, filePath: String): MutableMap<String, Any> {
        val response = mutableMapOf<String, Any>()
        if (FileUtils.isFileExists(filePath)) {
            FileIOUtils.writeFileFromString(filePath, content, false)
            response["code"] = 200
            response["success"] = true
        } else {
            response["code"] = 0
            response["success"] = false
        }
        return response
    }

}