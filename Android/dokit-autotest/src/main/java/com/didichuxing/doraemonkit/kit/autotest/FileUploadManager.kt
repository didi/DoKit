package com.didichuxing.doraemonkit.kit.autotest

import android.graphics.Bitmap
import com.didichuxing.doraemonkit.kit.test.report.ScreenShotManager
import com.didichuxing.doraemonkit.util.LogHelper
import kotlinx.coroutines.*


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

class FileUploadManager(private val screenShotManager: ScreenShotManager) {

    private val uploadScope = CoroutineScope(SupervisorJob() + Dispatchers.IO) + CoroutineName("upload")

    fun uploadBitmap(bitmap: Bitmap, fileName: String) {
        uploadScope.launch {
            screenShotManager.saveBitmap(bitmap, fileName)
            upload(screenShotManager.getScreenFile(fileName))
        }

    }


    private fun upload(fileName: String) {
        LogHelper.e("Autotest", "upload() fileName=${fileName}")
    }
}
