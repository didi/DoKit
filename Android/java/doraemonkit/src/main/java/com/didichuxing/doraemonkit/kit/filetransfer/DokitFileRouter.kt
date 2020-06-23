package com.didichuxing.doraemonkit.kit.filetransfer

import android.os.Build
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.PathUtils
import com.didichuxing.doraemonkit.kit.filetransfer.action.*
import com.didichuxing.doraemonkit.kit.filetransfer.bean.DirInfo
import com.fasterxml.jackson.databind.SerializationFeature
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.*
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.content.*
import io.ktor.jackson.jackson
import io.ktor.request.receive
import io.ktor.request.receiveMultipart
import io.ktor.response.respond
import io.ktor.response.respondFile
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.routing
import java.io.File
import java.io.InputStream
import java.time.Duration


/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020/6/23-14:35
 * 描    述：
 * 修订历史：
 * ================================================
 */
val DoKitFileRouter: Application.() -> Unit = {
    install(ContentNegotiation) {
        jackson {
            enable(SerializationFeature.INDENT_OUTPUT) // 美化输出 JSON
        }
    }
    install(CORS) {
        method(HttpMethod.Options)
        method(HttpMethod.Get)
        method(HttpMethod.Post)
        method(HttpMethod.Put)
        method(HttpMethod.Delete)
        method(HttpMethod.Patch)
        header(HttpHeaders.AccessControlAllowHeaders)
        header(HttpHeaders.ContentType)
        header(HttpHeaders.AccessControlAllowOrigin)
        allowCredentials = true
        anyHost()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            maxAge = Duration.ofDays(1L)
        }
    }
    install(DefaultHeaders)
    install(CallLogging)

    routing {
        static("custom") {
            staticRootFolder = File(PathUtils.getInternalAppDataPath())
            files("img")
        }

        /**
         * index
         */
        get("/") {
            call.respond(IndexAction.createIndexInfo())
        }

        /**
         * 获取设备详情
         */
        get("/getDeviceInfo") {
            call.respond(DeviceInfoAction.createDeviceInfo())
        }

        /**
         * 获取文件列表
         */
        get("/getFileList") {
            val queryParameters = call.request.queryParameters
            val filePath = queryParameters["filePath"]
            if (filePath.isNullOrBlank()) {
                call.respond(RequestErrorAction.createErrorInfo("filePath is not standard"))
            } else {
                call.respond(FileListAction.createFileList(filePath))
            }
        }

        /**
         * 获取文件详情
         */
        get("/getFileDetail") {
            val queryParameters = call.request.queryParameters
            val dirPath = queryParameters["filePath"]
            val fileType = queryParameters["fileType"]
            val fileName = queryParameters["fileName"]
            call.respond(FileDetailAction.createFileDetailInfo("$dirPath${File.separator}$fileName", fileType))
        }

        /**
         * 创建文件夹
         */
        post("/createFolder") {
            val params = call.receive<DirInfo>()
            val dirPath = params.filePath
            val fileName = params.fileName
            val response = mutableMapOf<String, Any>()
            if (FileUtils.isDir(dirPath)) {
                val createOrExistsFile = FileUtils.createOrExistsDir("$dirPath${File.separator}$fileName")
                if (createOrExistsFile) {
                    response["code"] = 200
                    response["success"] = true
                } else {
                    response["code"] = 0
                    response["success"] = false
                }
            } else {
                response["code"] = 0
                response["success"] = false
            }
            call.respond(response)
        }

        /**
         * 上传文件
         */
        post("/uploadFile") {
            val multipart = call.receiveMultipart()
            //val postParams = call.receiveParameters()
            //val dirPath = postParams["filePath"]
            var filePart: PartData.FileItem? = null
            var formPart: PartData.FormItem? = null
            multipart.forEachPart { part ->
                if (part is PartData.FileItem) {
                    filePart = part
                } else if (part is PartData.FormItem) {
                    formPart = part
                }
            }

            val dirPath = formPart?.value
            val fileName = filePart?.originalFileName
            val file = File("$dirPath${File.separator}$fileName")

            filePart?.let {
                it.streamProvider().use { inputStream: InputStream? ->
                    file.outputStream().buffered().use { outputStream ->
                        inputStream?.copyTo(outputStream)
                    }
                }
            }
            filePart?.dispose
            formPart?.dispose

            val response = mutableMapOf<String, Any>()
            if (FileUtils.isFileExists(file)) {
                response["code"] = 200
                response["success"] = true
            } else {
                response["code"] = 0
                response["success"] = false
            }

            call.respond(response)

        }

        /**
         * 下载文件
         */
        get("/downloadFile") {
            val queryParameters = call.request.queryParameters
            val dirPath = queryParameters["filePath"]
            val fileName = queryParameters["fileName"]

            val file = File("$dirPath${File.separator}$fileName")
            if (FileUtils.isFileExists(file)) {
                call.respondFile(file)
            } else {
                val response = mutableMapOf<String, Any>()
                response["code"] = 0
                response["success"] = false
                call.respond(response)
            }

        }

        /**
         * 删除文件
         */
        post("/deleteFile") {
            val params = call.receive<DirInfo>()
            val dirPath = params.filePath
            val fileName = params.fileName
            val filePath = "$dirPath${File.separator}$fileName"
            val response = mutableMapOf<String, Any>()
            //删除文件夹
            if (FileUtils.isFileExists(filePath) && FileUtils.isDir(filePath)) {
                val deleteSuccess = FileUtils.deleteAllInDir("$dirPath${File.separator}$fileName")
                if (deleteSuccess) {
                    response["code"] = 200
                    response["success"] = true
                } else {
                    response["code"] = 0
                    response["success"] = false
                }
            }
            //删除文件
            else if (FileUtils.isFileExists(filePath) && FileUtils.isFile(dirPath)) {
                val deleteSuccess = FileUtils.delete("$dirPath${File.separator}$fileName")
                if (deleteSuccess) {
                    response["code"] = 200
                    response["success"] = true
                } else {
                    response["code"] = 0
                    response["success"] = false
                }
            } else {
                response["code"] = 0
                response["success"] = false
            }
            call.respond(response)
        }

    }
}





