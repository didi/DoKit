package com.didichuxing.doraemonkit.kit.filemanager.action.file

import com.didichuxing.doraemonkit.util.FileIOUtils
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
object FileDetailAction {
    fun fileDetailInfoRes(filePath: String): MutableMap<String, Any> {
        val params = mutableMapOf<String, Any>()
        if (FileUtils.isFileExists(filePath) && FileUtils.isFile(filePath)) {
            params["code"] = 200
            val data = mutableMapOf<String, Any>()
            data["fileType"] = FileUtils.getFileExtension(filePath)
            data["fileContent"] = FileIOUtils.readFile2String(filePath)
            params["data"] = data
        } else {
            params["code"] = 0
            params["data"] = "$filePath is not a file"
        }

        return params
    }
}



