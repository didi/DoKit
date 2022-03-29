package com.didichuxing.doraemonkit.kit.mc.net

import android.text.TextUtils
import com.didichuxing.doraemonkit.kit.core.DoKitManager
import com.didichuxing.doraemonkit.constant.WSEType
import com.didichuxing.doraemonkit.constant.WSMode
import com.didichuxing.doraemonkit.kit.mc.ability.WSEventProcessor
import com.didichuxing.doraemonkit.kit.mc.all.*
import com.didichuxing.doraemonkit.kit.mc.all.DokitMcConnectManager
import com.didichuxing.doraemonkit.kit.mc.mock.proxy.McProxyManager
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

object DoKitMcConnectClient {
    const val TAG = "DoKitWsConnectServer"
    private var clientWebSocketSession: DefaultClientWebSocketSession? = null
    const val CONNECT_SUCCEED = 200
    const val CONNECT_FAILED = 0

    var authLoginData: LoginData? = null

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

    fun connect(host: String, port: Int, path: String, connectSerial: String, callBack: suspend (Int, String?) -> Unit) {

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val client = client.ws(
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

                    onConnectSuccess(connectSerial)
                    DokitMcConnectManager.connectMode = ConnectMode.CONNECT
                    DoKitManager.WS_MODE = WSMode.CLIENT

                    clientWebSocketSession?.let {
                        CoroutineScope(it.coroutineContext).launch {

                        }
                    }

                    incoming.consumeEach {
                        try {
                            onHandleMessage(it, this, callBack)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }

                }

            } catch (e: Exception) {
                e.printStackTrace()
                LogHelper.e(TAG, "client connect error===>${e.message}")
                callBack(CONNECT_FAILED, e.message)

            }

        }
    }

    private fun onConnectSuccess(connectSerial: String) {
        login(connectSerial)
    }

    private suspend fun onHandleMessage(it: Frame, session: DefaultClientWebSocketSession, callBack: suspend (Int, String?) -> Unit) {
        when (it) {
            is Frame.Text -> {
                val packageText = it.readText()
                LogHelper.json(TAG, packageText)
                val text = GsonUtils.fromJson<WSPackage>(
                    packageText,
                    WSPackage::class.java
                )
                if (text?.type != null) {
                    when (text.type) {
                        PackageType.LOGIN -> {
                            DokitMcConnectManager.connectMode = ConnectMode.CONNECT
                            val data = text.data

                            try {
                                val loginData = GsonUtils.fromJson<LoginData>(data, LoginData::class.java)
                                authLoginData = loginData
                                callBack(CONNECT_SUCCEED, data)
                            } catch (e: Exception) {
                                e.printStackTrace()
                                LogHelper.e(TAG, "client handle error===>${e.message}")
                            }
                        }
                        PackageType.NOTIFY -> {
                            LogHelper.e(TAG, "client handle notify===>${text.data}")
                        }
                        PackageType.BROADCAST -> {
                            if (TextUtils.equals(text.pid, "001")) {
                                DokitMcConnectManager.changeClientMode()
                            } else {
                                val serverInfo = text.data
                                try {
                                    process(serverInfo)
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                    LogHelper.e(TAG, "client handle error===>${e.message}")
                                }
                            }
                        }
                        PackageType.DATA -> {
                            McProxyManager.dispatch(packageText)
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

    fun close() {
        try {
            clientWebSocketSession?.let {
                CoroutineScope(it.coroutineContext).launch {
                    clientWebSocketSession?.close()
                }
            }
            DokitMcConnectManager.connectMode = ConnectMode.CLOSE
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }


    fun send(wsEvent: WSEvent) {
        clientWebSocketSession?.let {
            CoroutineScope(it.coroutineContext).launch {
                if (it.isActive) {
                    val wsPackage = WSPackageUtils.toPackageJson(wsEvent)
                    it.outgoing.send(Frame.Text(wsPackage))
                }
            }
        }
    }

    fun sendDataProxy(proxyPackage: String) {
        clientWebSocketSession?.let {
            CoroutineScope(it.coroutineContext).launch {
                if (it.isActive) {
                    it.outgoing.send(Frame.Text(proxyPackage))
                }
            }
        }
    }

    fun sendChangeHostMode() {
        clientWebSocketSession?.let {
            CoroutineScope(it.coroutineContext).launch {
                if (it.isActive) {
                    val wsPackage = WSPackageUtils.toPackageJson("001", PackageType.BROADCAST, "")
                    it.outgoing.send(Frame.Text(wsPackage))
                }
            }
        }
    }

    fun getConnectSerial(): String {
        authLoginData?.let {
            return it.connectSerial
        }
        return ""
    }

    private fun login(connectSerial: String) {
        clientWebSocketSession?.let {
            CoroutineScope(it.coroutineContext).launch {
                if (it.isActive) {
                    val pi = DokitDeviceUtils.getPackageInfo(Utils.getApp())
                    val name = "${DeviceUtils.getManufacturer()}-${DeviceUtils.getModel()}(${DeviceUtils.getSDKVersionName()})"
                    val loginData = LoginData(
                        name,
                        "android",
                        "${DeviceUtils.getSDKVersionName()}",
                        "${UIUtils.getWidthPixels()} x ${UIUtils.getRealHeightPixels()}",
                        "${DeviceUtils.getManufacturer()}-${DeviceUtils.getModel()}",
                        "${DoKitManager.IP_ADDRESS_BY_WIFI}",
                        connectSerial,
                        "${pi.packageName}",
                        "${pi.versionName}"
                    )
                    val data = GsonUtils.toJson(loginData)
                    it.outgoing.send(Frame.Text(WSPackageUtils.toPackageJson("000", PackageType.LOGIN, data)))
                }
            }
        }
    }

    fun session(): DefaultClientWebSocketSession? {
        return clientWebSocketSession;
    }


}




