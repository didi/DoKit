package com.didichuxing.doraemonkit.kit.filemanager.action.file

import com.didichuxing.doraemonkit.util.FileUtils
import java.io.File

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020/6/23-15:26
 * 描    述：
 * 修订历史：
 * ================================================
 */
object CreateFolderAction {
    fun createFolderRes(dirPath: String, fileName: String): MutableMap<String, Any> {
        val response = mutableMapOf<String, Any>()
        if (FileUtils.isDir(dirPath)) {
            val createOrExistsFile = FileUtils.createOrExistsDir("$dirPath${File.separator}$fileName")
            if (createOrExistsFile) {
                response["code"] = 200
                response["success"] = true
                response["message"] = "success"
            } else {
                response["code"] = 0
                response["success"] = false
                response["message"] = "create dir failure"
            }
        } else {
            response["code"] = 0
            response["success"] = false
            response["message"] = "is not dir"
        }

        return response
    }

}