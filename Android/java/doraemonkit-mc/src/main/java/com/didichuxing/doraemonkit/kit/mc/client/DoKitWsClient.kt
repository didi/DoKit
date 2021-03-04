package com.didichuxing.doraemonkit.kit.mc.client

import com.blankj.utilcode.util.DeviceUtils
import com.blankj.utilcode.util.GsonUtils
import com.blankj.utilcode.util.ToastUtils
import com.didichuxing.doraemonkit.constant.DoKitConstant
import com.didichuxing.doraemonkit.constant.WSEType
import com.didichuxing.doraemonkit.constant.WSMode
import com.didichuxing.doraemonkit.kit.mc.all.*
import com.didichuxing.doraemonkit.util.LogHelper
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.features.websocket.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.http.cio.websocket.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.consumeEach
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

object DoKitWsClient {
    const val TAG = "DoKitWSClient"
    private var clientWebSocketSession: DefaultClientWebSocketSession? = null
    const val CONNECT_SUCCEED = 200
    const val CONNECT_FAIL = 0

    private val client: HttpClient by lazy {
        try {
            Class.forName("io.ktor.client.engine.okhttp.OkHttp")
            LogHelper.i(TAG, "client engine is OkHttp")
            return@lazy HttpClient(OkHttp) {
                install(WebSockets)
            }
        } catch (e: ClassNotFoundException) {
            LogHelper.i(TAG, "client engine is CIO")
            return@lazy HttpClient(CIO) {
                install(WebSockets)
            }
        }
    }

    fun connect(host: String, port: Int, path: String, callBack: suspend (Int, String?) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                client.ws(
                    method = HttpMethod.Get,
                    host = host,
                    port = port,
                    path = path,
                    request = {
                        headers {
                            header(
                                "deviceModel",
                                "${DeviceUtils.getManufacturer()}-${DeviceUtils.getModel()}"
                            )
                        }
                    }
                ) {
                    DoKitConstant.WS_MODE = WSMode.CLIENT
                    clientWebSocketSession = this
                    /**
                     * 避免ws在收到第一条消息以后 通道自动关闭的问题
                     * https://github.com/ktorio/ktor/issues/402
                     */
                    incoming.consumeEach {
                        when (it) {
                            is Frame.Text -> {
                                val serverInfo = it.readText()
                                LogHelper.json(TAG, serverInfo)
                                try {
                                    val wsEvent =
                                        GsonUtils.fromJson<WSEvent>(
                                            serverInfo,
                                            WSEvent::class.java
                                        )
                                    //连接成功的返回信息
                                    if (wsEvent.eventType == WSEType.WSE_CONNECTED) {
                                        callBack(
                                            CONNECT_SUCCEED,
                                            wsEvent.commParams?.get("hostInfo")
                                        )
                                    }

                                    //断开连接
                                    if (wsEvent.eventType == WSEType.WSE_CLOSE) {
                                        close(
                                            CloseReason(
                                                CloseReason.Codes.NORMAL,
                                                "Server said BYE"
                                            )
                                        )
                                        ToastUtils.showShort("已从主机【${McConstant.HOST_INFO?.deviceName}】断开")
                                    }


                                    WSEventProcessor.process(wsEvent)
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }

                            }
                            else -> {

                            }
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                //LogHelper.i(TAG, "client connect error===>${e.message}")
                callBack(CONNECT_FAIL, e.message)

            }

        }
    }

    fun close() {
        send(
            WSEvent(
                WSMode.CLIENT,
                WSEType.WSE_CLOSE,
                mutableMapOf("command" to "bye"),
                null
            )
        )
        //client.close()
        DoKitConstant.WS_MODE = WSMode.UNKNOW
    }


    fun send(wsEvent: WSEvent) {
        clientWebSocketSession?.let {
            CoroutineScope(it.coroutineContext).launch {
                if (it.isActive) {
                    it.outgoing.send(Frame.Text(GsonUtils.toJson(wsEvent)))
                }
            }
        }


    }


}




