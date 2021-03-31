package com.didichuxing.doraemonkit.kit.filemanager

import android.os.Build
import com.didichuxing.doraemonkit.util.FileUtils
import com.didichuxing.doraemonkit.kit.filemanager.action.RequestErrorAction
import com.didichuxing.doraemonkit.kit.filemanager.action.file.*
import com.didichuxing.doraemonkit.kit.filemanager.action.sql.DatabaseAction
import com.didichuxing.doraemonkit.kit.filemanager.bean.DirInfo
import com.didichuxing.doraemonkit.kit.filemanager.bean.RenameFileInfo
import com.didichuxing.doraemonkit.kit.filemanager.bean.SaveFileInfo
import com.didichuxing.doraemonkit.kit.filemanager.convert.GsonConverter
import com.didichuxing.doraemonkit.kit.filemanager.sqlite.bean.RowRequestInfo
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.*
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.request.receive
import io.ktor.request.receiveMultipart
import io.ktor.response.respond
import io.ktor.response.respondFile
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.routing

import java.io.File
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
        register(ContentType.Application.Json, GsonConverter())
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
//        static("custom") {
//            staticRootFolder = File(PathUtils.getInternalAppDataPath())
//            files("img")
//        }

        /**
         * index
         */
        get("/") {
            call.respond(IndexAction.indexInfoRes())
        }

        /**
         * 获取设备详情
         */
        get("/getDeviceInfo") {
            call.respond(DeviceInfoAction.deviceInfoRes())
        }

        /**
         * 获取文件列表
         */
        get("/getFileList") {
            val queryParameters = call.request.queryParameters
            val dirPath = FileManagerUtil.absoluteRootPath(queryParameters["dirPath"])
            if (dirPath.isBlank()) {
                call.respond(RequestErrorAction.createErrorInfo("dirPath is not standard"))
            } else {
                call.respond(FileListAction.fileListRes(dirPath))
            }
        }

        /**
         * 获取文件详情
         */
        get("/getFileDetail") {
            val queryParameters = call.request.queryParameters
            val dirPath = FileManagerUtil.absoluteRootPath(queryParameters["dirPath"])
//            val fileType = queryParameters["fileType"]
            val fileName = queryParameters["fileName"]
            val filePath = "$dirPath$fileName"
            call.respond(FileDetailAction.fileDetailInfoRes(filePath))
        }

        /**
         * 创建文件夹
         */
        post("/createFolder") {
            val params = call.receive<DirInfo>()
            val dirPath = FileManagerUtil.absoluteRootPath(params.dirPath)
            val fileName = params.fileName
            call.respond(CreateFolderAction.createFolderRes(dirPath, fileName))
        }

        /**
         * 上传文件
         */
        post("/uploadFile") {
            val multipart = call.receiveMultipart()
            call.respond(UploadFileAction.uploadFileRes(multipart))
        }

        /**
         * 下载文件
         */
        get("/downloadFile") {
            val queryParameters = call.request.queryParameters
            val dirPath = FileManagerUtil.absoluteRootPath(queryParameters["dirPath"])
            val fileName = queryParameters["fileName"]

            val file = File("$dirPath${File.separator}$fileName")
            if (FileUtils.isFileExists(file)) {
                //call.response.header("Content-Disposition", "attachment; filename=\"${file.name}\"")
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
            val dirPath = FileManagerUtil.absoluteRootPath(params.dirPath)
            val fileName = params.fileName
            val filePath = "$dirPath$fileName"
            call.respond(DeleteFileAction.deleteFileRes(filePath, dirPath, fileName))
        }

        /**
         * 重命名文件
         */
        post("/rename") {
            val fileInfo = call.receive<RenameFileInfo>()
            val dirPath = FileManagerUtil.absoluteRootPath(fileInfo.dirPath)
            val oldName = fileInfo.oldName
            val filePath = "$dirPath$oldName"
            call.respond(RenameFileAction.renameFileRes(fileInfo.newName, filePath))
        }


        /**
         * 保存文件
         */
        post("/saveFile") {
            val saveFileInfo = call.receive<SaveFileInfo>()
            val dirPath = FileManagerUtil.absoluteRootPath(saveFileInfo.dirPath)
            val fileName = saveFileInfo.fileName
            val content = saveFileInfo.content
            val filePath = "$dirPath$fileName"
            call.respond(SaveFileAction.saveFileRes(content, filePath))
        }

        /**
         * 数据库相关接口
         */

        get("/getAllTable") {
            val queryParameters = call.request.queryParameters
            val dirPath = FileManagerUtil.absoluteRootPath(queryParameters["dirPath"])
            val fileName = queryParameters["fileName"]
            val filePath = "$dirPath$fileName"
            call.respond(DatabaseAction.allTablesRes(filePath, fileName!!))
        }

        /**
         * 查询指定表中的数据
         */
        get("/getTableData") {
            val queryParameters = call.request.queryParameters
            val dirPath = FileManagerUtil.absoluteRootPath(queryParameters["dirPath"])
            val fileName = queryParameters["fileName"]
            val tableName = queryParameters["tableName"]
            val filePath = "$dirPath$fileName"
            call.respond(DatabaseAction.tableDatasRes(filePath, fileName!!, tableName!!))
        }

        /**
         * 插入一行数据
         */
        post("/insertRow") {
            val rowRequestInfo = call.receive<RowRequestInfo>()
            val dirPath = FileManagerUtil.absoluteRootPath(rowRequestInfo.dirPath)
            val fileName = rowRequestInfo.fileName
            val tableName = rowRequestInfo.tableName
            val filePath = "$dirPath$fileName"
            val rowDatas = rowRequestInfo.rowDatas
            call.respond(DatabaseAction.insertRowRes(filePath, fileName, tableName, rowDatas))
        }

        /**
         * 更新一条数据
         */
        post("/updateRow") {
            val rowRequestInfo = call.receive<RowRequestInfo>()
            val dirPath = FileManagerUtil.absoluteRootPath(rowRequestInfo.dirPath)
            val fileName = rowRequestInfo.fileName
            val tableName = rowRequestInfo.tableName
            val filePath = "$dirPath$fileName"
            val rowDatas = rowRequestInfo.rowDatas
            call.respond(DatabaseAction.updateRowRes(filePath, fileName, tableName, rowDatas))
        }

        /**
         * 删除一条数据
         */
        post("/deleteRow") {
            val rowRequestInfo = call.receive<RowRequestInfo>()
            val dirPath = FileManagerUtil.absoluteRootPath(rowRequestInfo.dirPath)
            val fileName = rowRequestInfo.fileName
            val tableName = rowRequestInfo.tableName
            val filePath = "$dirPath$fileName"
            val rowDatas = rowRequestInfo.rowDatas
            call.respond(DatabaseAction.deleteRowRes(filePath, fileName, tableName, rowDatas))
        }

    }
}





