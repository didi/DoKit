package com.didichuxing.doraemonkit.kit.mc.server

import com.didichuxing.doraemonkit.util.GsonUtils
import com.didichuxing.doraemonkit.constant.DoKitConstant
import com.didichuxing.doraemonkit.constant.WSMode
import com.didichuxing.doraemonkit.kit.mc.all.WSEvent
import io.ktor.http.cio.websocket.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import io.ktor.websocket.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020/11/13-15:38
 * 描    述：
 * 修订历史：
 * ================================================
 */
object DoKitWsServer {
    /**
     * 所有的连接
     */
    internal val wsSessionMaps: MutableMap<String?, DefaultWebSocketServerSession> = mutableMapOf()

    private val server: CIOApplicationEngine by lazy {
        embeddedServer(CIO, port = 4444, module = WSRouter)
    }
    //val engine

    fun start(callBack: () -> Unit) {
        try {
            server.start()
            DoKitConstant.WS_MODE = WSMode.HOST
            callBack()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    fun stop() {
        try {
            server.stop(1, 1)
            DoKitConstant.WS_MODE = WSMode.UNKNOW
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun send(wsEvent: WSEvent) {
        wsSessionMaps.forEach {
            CoroutineScope(it.value.coroutineContext).launch {
                if (it.value.isActive) {
                    it.value.outgoing.send(Frame.Text(GsonUtils.toJson(wsEvent)))
                }
            }
        }
    }
}