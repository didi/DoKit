package com.didichuxing.doraemonkit.kit.mc.server

import com.didichuxing.doraemonkit.kit.core.DoKitManager
import com.didichuxing.doraemonkit.constant.WSEType
import com.didichuxing.doraemonkit.constant.WSMode
import com.didichuxing.doraemonkit.kit.mc.all.WSEvent
import com.didichuxing.doraemonkit.util.GsonUtils
import io.ktor.http.cio.websocket.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import io.ktor.websocket.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
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
    val wsSessionMaps: MutableMap<String?, DefaultWebSocketServerSession> = mutableMapOf()

    private val server: CIOApplicationEngine by lazy {
        embeddedServer(CIO, port = DoKitManager.MC_WS_PORT, module = WSRouter)
    }
    //val engine

    fun start(callBack: () -> Unit) {
        try {
            server.start()
            DoKitManager.WS_MODE = WSMode.HOST
            callBack()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    suspend fun stop(callBack: () -> Unit) {
        try {
            send(WSEvent(WSMode.HOST, WSEType.WSE_HOST_CLOSE))
            delay(1000)
            wsSessionMaps.forEach {
                it.value.close()
            }
            wsSessionMaps.clear()
            //server.stop(1, 1)
            DoKitManager.WS_MODE = WSMode.UNKNOW
            callBack()
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