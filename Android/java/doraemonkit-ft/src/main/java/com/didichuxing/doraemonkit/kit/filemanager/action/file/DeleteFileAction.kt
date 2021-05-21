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
object DeleteFileAction {
    fun deleteFileRes(filePath: String, dirPath: String, fileName: String): MutableMap<String, Any> {
        val response = mutableMapOf<String, Any>()
        //删除文件夹
        if (FileUtils.isFileExists(filePath)) {
            //假如是文件夹
            if (FileUtils.isDir(filePath)) {
                //先删除文件夹下的所有内容
                val deleteFilesSuccess = FileUtils.deleteAllInDir("$dirPath${File.separator}$fileName")
                //再删除文件夹本身
                val deleteDirSuccess = FileUtils.delete("$dirPath${File.separator}$fileName")
                if (deleteFilesSuccess && deleteDirSuccess) {
                    response["code"] = 200
                    response["success"] = true
                    response["message"] = "success"
                } else {
                    response["code"] = 0
                    response["success"] = false
                    response["message"] = "delete $filePath failure"
                }
            } else {
                //删除文件
                val deleteSuccess = FileUtils.delete("$dirPath${File.separator}$fileName")
                if (deleteSuccess) {
                    response["code"] = 200
                    response["success"] = true
                    response["message"] = "success"
                } else {
                    response["code"] = 0
                    response["success"] = false
                    response["message"] = "delete $filePath failure"
                }
            }
        } else {
            response["code"] = 0
            response["success"] = false
            response["message"] = "delete $filePath failure"
        }

        return response
    }

}