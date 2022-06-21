package com.didichuxing.doraemonkit.kit.test.report

import android.graphics.Bitmap
import com.didichuxing.doraemonkit.kit.connect.data.PackageType
import com.didichuxing.doraemonkit.kit.connect.parser.ByteParser
import com.didichuxing.doraemonkit.kit.connect.parser.JsonParser
import com.didichuxing.doraemonkit.kit.connect.ws.WebSocketClient
import com.didichuxing.doraemonkit.util.FileIOUtils
import com.didichuxing.doraemonkit.util.RandomUtils
import kotlinx.coroutines.*
import java.io.ByteArrayOutputStream


/**
 * didi Create on 2022/4/18 .
 *
 * Copyright (c) 2022/4/18 by didiglobal.com.
 *
 * @author <a href="realonlyone@126.com">zhangjun</a>
 * @version 1.0
 * @Date 2022/4/18 4:24 下午
 * @Description 上传文件实现 能将指定文件上传到指定地址
 */

class FileUploadManager(
    private val screenShotManager: ScreenShotManager,
    private val webSocketClient: WebSocketClient
) {

    private val uploadScope = CoroutineScope(SupervisorJob() + Dispatchers.IO) + CoroutineName("upload")

    fun uploadBitmap(bitmap: Bitmap, fileName: String) {
        uploadScope.launch {
            screenShotManager.saveBitmap(bitmap, fileName)
//            upload(screenShotManager.getScreenFile(fileName))
        }
    }

    fun uploadBitmap(bitmap: Bitmap) {
        uploadScope.launch {
            upload(bitmap, screenShotManager.createNextFileName())
        }
    }

    private fun upload(bitmap: Bitmap, fileName: String) {
        //保存图片
        val byteArray = ByteArrayOutputStream(2048)
        val ok = bitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArray)
        val bytes = byteArray.toByteArray()

        val dataMap = mutableMapOf<String, String>()
        dataMap["caseId"] = RandomUtils.random32HexString()
        dataMap["image"] = fileName
        dataMap["type"] = "jpeg"

        val textPackage = JsonParser.toTextPackage(PackageType.AUTOTEST, dataMap, "image")
        val byteString = ByteParser.toByteString(textPackage, bytes)

        webSocketClient.send(byteString)
    }


    private fun upload(fileName: String) {
        val bytes = FileIOUtils.readFile2BytesByChannel(fileName)
        val dataMap = mutableMapOf<String, String>()
        dataMap["caseId"] = RandomUtils.random32HexString()
        dataMap["image"] = fileName
        dataMap["type"] = "jpeg"

        val textPackage = JsonParser.toTextPackage(PackageType.AUTOTEST, dataMap, "image")
        val byteString = ByteParser.toByteString(textPackage, bytes)
        webSocketClient.send(byteString)
    }
}
