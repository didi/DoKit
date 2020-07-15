package com.didichuxing.doraemonkit.kit.filemanager.sqlite.http

import com.didichuxing.doraemonkit.constant.DokitConstant
import com.didichuxing.doraemonkit.kit.filemanager.DoKitFileRouter
import io.ktor.server.cio.CIO
import io.ktor.server.cio.CIOApplicationEngine
import io.ktor.server.engine.embeddedServer

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020/6/28-11:02
 * 描    述：
 * 修订历史：
 * ================================================
 */
object HttpServer {
    val server: CIOApplicationEngine by lazy {
        embeddedServer(CIO, port = DokitConstant.FILE_MANAGER_HTTP_PORT, module = DoKitFileRouter)
    }
}