package com.didichuxing.doraemonkit.kit.filemanager.action.file

import com.didichuxing.doraemonkit.util.FileUtils

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020/6/23-15:26
 * 描    述：
 * 修订历史：
 * ================================================
 */
object RenameFileAction {
    fun renameFileRes(newName: String, filePath: String): MutableMap<String, Any> {
        val response = mutableMapOf<String, Any>()
        if (FileUtils.isFileExists(filePath)) {
            FileUtils.rename(filePath, newName)
            response["code"] = 200
            response["success"] = true
            response["message"] = "success"
        } else {
            response["code"] = 0
            response["success"] = false
            response["message"] = "$filePath is not exists"
        }
        return response
    }

}