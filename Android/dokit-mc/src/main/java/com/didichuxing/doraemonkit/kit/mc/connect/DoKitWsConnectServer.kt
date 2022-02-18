package com.didichuxing.doraemonkit.kit.mc.connect

import com.didichuxing.doraemonkit.kit.core.DoKitManager
import com.didichuxing.doraemonkit.constant.WSEType
import com.didichuxing.doraemonkit.constant.WSMode
import com.didichuxing.doraemonkit.kit.mc.all.*
import com.didichuxing.doraemonkit.kit.mc.util.WSPackageUtils
import com.didichuxing.doraemonkit.util.*
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.features.websocket.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.http.cio.websocket.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.consumeEach

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020/11/13-15:38
 * 描    述：
 * 修订历史：
 * ================================================
 */

object DoKitWsConnectServer {
    const val TAG = "DoKitWsConnectServer"
    private var clientWebSocketSession: DefaultClientWebSocketSession? = null
    const val CONNECT_SUCCEED = 200
    const val CONNECT_FAILED = 0

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
                    DokitMcConnectManager.connectMode = ConnectMode.LOGIN
                    clientWebSocketSession = this

                    onConnectSuccess()
                    callBack(CONNECT_SUCCEED, "连接成功")
                    DokitMcConnectManager.connectMode = ConnectMode.CONNECT

                    onHandle(this, callBack)

                }
            } catch (e: Exception) {
                e.printStackTrace()
                LogHelper.e(TAG, "client connect error===>${e.message}")
                callBack(CONNECT_FAILED, e.message)

            }

        }
    }

    private fun onConnectSuccess() {
        login()
    }

    private suspend fun onHandle(session: DefaultClientWebSocketSession, callBack: suspend (Int, String?) -> Unit) {
        /**
         * 避免ws在收到第一条消息以后 通道自动关闭的问题
         * https://github.com/ktorio/ktor/issues/402
         */

        session.incoming.consumeEach {
            when (it) {
                is Frame.Text -> {
                    val packageText = it.readText()
                    LogHelper.json(TAG, packageText)
                    val text = GsonUtils.fromJson<WSPackage>(
                        packageText,
                        WSPackage::class.java
                    )
                    if (text != null && text.type != null) {
                        when (text.type) {
                            PackageType.LOGIN -> {
                                DokitMcConnectManager.connectMode = ConnectMode.CONNECT
                            }
                            PackageType.NOTIFY -> {

                            }
                            PackageType.BROADCAST -> {
                                val serverInfo = text.data
                                try {
                                    process(serverInfo)
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                    LogHelper.e(TAG, "client handle error===>${e.message}")
                                }
                            }
                        }
                    }
                }

                is Frame.Binary -> {
                    LogHelper.i(TAG, "Binary data=${it}")
                }
                is Frame.Close -> {
                    LogHelper.i(TAG, "Close data=${it}")
                }
                is Frame.Ping -> {
                    LogHelper.i(TAG, "Ping data=${it}")
                }
                is Frame.Pong -> {
                    LogHelper.i(TAG, "Pong data=${it}")
                }
                else -> {
                    LogHelper.e(TAG, "type error===>${it}")
                }
            }
        }
    }

    private suspend fun process(serverInfo: String) {
        val wsEvent =
            GsonUtils.fromJson<WSEvent>(
                serverInfo,
                WSEvent::class.java
            )
        //连接成功的返回信息
        if (wsEvent.eventType == WSEType.WSE_CONNECTED) {

        }

        //断开连接
        if (wsEvent.eventType == WSEType.WSE_CLOSE) {

        }

        WSEventProcessor.process(wsEvent)
    }

    suspend fun close() {
        try {
            clientWebSocketSession?.close()
            DokitMcConnectManager.connectMode = ConnectMode.CLOSE
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }


    fun send(wsEvent: WSEvent) {
        clientWebSocketSession?.let {
            CoroutineScope(it.coroutineContext).launch {
                if (it.isActive) {
                    val text = WSPackage("000", PackageType.BROADCAST, GsonUtils.toJson(wsEvent))
                    it.outgoing.send(Frame.Text(GsonUtils.toJson(wsEvent)))
                }
            }
        }
    }

    private fun login() {
        clientWebSocketSession?.let {
            CoroutineScope(it.coroutineContext).launch {
                if (it.isActive) {
                    val pi = DokitDeviceUtils.getPackageInfo(Utils.getApp())
                    val loginData = LoginData(
                        "",
                        "android",
                        "${DeviceUtils.getSDKVersionName()}",
                        "${UIUtils.getWidthPixels()} x ${UIUtils.getRealHeightPixels()}",
                        "${DeviceUtils.getManufacturer()}-${DeviceUtils.getModel()}",
                        "${DoKitManager.IP_ADDRESS_BY_WIFI}",
                        "",
                        "${pi.packageName}",
                        "${pi.versionName}"
                    )
                    val data = GsonUtils.toJson(loginData)
                    it.outgoing.send(Frame.Text(WSPackageUtils.toPackageJson("222", PackageType.LOGIN, data)))
                }
            }
        }
    }

    fun session(): DefaultClientWebSocketSession? {
        return clientWebSocketSession;
    }


}




