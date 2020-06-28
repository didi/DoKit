package com.didichuxing.doraemonkit.kit.filemanager.action.file

import com.blankj.utilcode.util.FileUtils
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
object DeleteFileAction {
    fun createDeleteRes(filePath: String, dirPath: String, fileName: String): MutableMap<String, Any> {
        val response = mutableMapOf<String, Any>()
        //删除文件夹
        if (FileUtils.isFileExists(filePath)) {
            //假如是文件夹
            if (FileUtils.isDir(filePath)) {
                val deleteSuccess = FileUtils.deleteAllInDir("$dirPath${File.separator}$fileName")
                if (deleteSuccess) {
                    response["code"] = 200
                    response["success"] = true
                } else {
                    response["code"] = 0
                    response["success"] = false
                }
            } else {
                //删除文件
                val deleteSuccess = FileUtils.delete("$dirPath${File.separator}$fileName")
                if (deleteSuccess) {
                    response["code"] = 200
                    response["success"] = true
                } else {
                    response["code"] = 0
                    response["success"] = false
                }
            }
        } else {
            response["code"] = 0
            response["success"] = false
        }

        return response
    }

}