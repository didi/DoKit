package com.didichuxing.doraemonkit.kit.filemanager.action.file

import com.didichuxing.doraemonkit.util.FileUtils
import com.didichuxing.doraemonkit.kit.filemanager.FileManagerUtil
import io.ktor.http.content.MultiPartData
import io.ktor.http.content.PartData
import io.ktor.http.content.forEachPart
import io.ktor.http.content.streamProvider
import java.io.File
import java.io.InputStream

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020/6/23-15:26
 * 描    述：
 * 修订历史：
 * ================================================
 */
object UploadFileAction {

    suspend fun uploadFileRes(multipart: MultiPartData): MutableMap<String, Any> {
        //val postParams = call.receiveParameters()
        //val dirPath = postParams["filePath"]
        var filePart: PartData.FileItem? = null
        var formPart: PartData.FormItem? = null
        multipart.forEachPart { part ->
            when (part) {
                is PartData.FileItem -> filePart = part
                is PartData.FormItem -> formPart = part
            }
        }

        val response = mutableMapOf<String, Any>()

        filePart?.let {
            val dirPath = FileManagerUtil.absoluteRootPath(formPart?.value)
            val fileName = filePart?.originalFileName
            val file = File("$dirPath${File.separator}$fileName")

            it.streamProvider().use { inputStream: InputStream? ->
                file.outputStream().buffered().use { outputStream ->
                    inputStream?.copyTo(outputStream)
                }
            }
            filePart?.dispose
            formPart?.dispose

            if (FileUtils.isFileExists(file)) {
                response["code"] = 200
                response["success"] = true
                response["message"] = "success"
            } else {
                response["code"] = 0
                response["success"] = false
                response["message"] = "${file.absolutePath} is not exists"
            }
        } ?: let {
            response["code"] = 0
            response["success"] = false
            response["message"] = "filePart is null"
        }


        return response
    }

}