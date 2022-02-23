package com.didichuxing.doraemonkit.kit.mc.net

import androidx.appcompat.app.AlertDialog
import com.didichuxing.doraemonkit.DoKit
import com.didichuxing.doraemonkit.kit.core.DoKitManager
import com.didichuxing.doraemonkit.constant.WSEType
import com.didichuxing.doraemonkit.constant.WSMode
import com.didichuxing.doraemonkit.extension.doKitGlobalScope
import com.didichuxing.doraemonkit.kit.mc.ability.WSClientProcessor
import com.didichuxing.doraemonkit.kit.mc.ability.WSEventProcessor
import com.didichuxing.doraemonkit.kit.mc.all.*
import com.didichuxing.doraemonkit.kit.mc.all.data.HostInfo
import com.didichuxing.doraemonkit.kit.mc.all.ui.client.ClientDokitView
import com.didichuxing.doraemonkit.kit.mc.all.DokitMcConnectManager
import com.didichuxing.doraemonkit.kit.mc.util.WSPackageUtils
import com.didichuxing.doraemonkit.util.*
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

object DoKitMcClient {
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

    fun connect(host: String, port: Int, path: String, callBack: suspend (Int, HostInfo?) -> Unit) {
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
                    DoKitManager.WS_MODE = WSMode.CLIENT
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
                                    val wsEvent = WSPackageUtils.jsonToEvent(it.readText())
                                    //连接成功的返回信息
                                    when (wsEvent.eventType) {

                                        WSEType.WSE_CONNECTED -> {
                                            val mHostInfo = GsonUtils.fromJson<HostInfo>(wsEvent.commParams?.get("hostInfo"), HostInfo::class.java)
                                            callBack(CONNECT_SUCCEED, mHostInfo)
                                            mHostInfo.let {
                                                ToastUtils.showShort("已经连接到${it.deviceName}主机")
                                            }
                                        }
                                        /**
                                         * 主机断开
                                         */
                                        WSEType.WSE_HOST_CLOSE -> {
                                            doKitGlobalScope.launch {
                                                DoKitMcClient.close()
                                            }
                                            DoKit.removeFloating(ClientDokitView::class)
                                            if (ActivityUtils.getTopActivity() != null) {
                                                AlertDialog.Builder(ActivityUtils.getTopActivity())
                                                    .setTitle("一机多控")
                                                    .setMessage("主机已断开连接！")
                                                    .setPositiveButton("确认") { dialog, _ ->
                                                        dialog.dismiss()
                                                    }
                                                    .setNegativeButton("取消") { dialog, _ ->
                                                        dialog.dismiss()
                                                    }
                                                    .show()
                                            }
                                        }

                                        WSEType.WSE_TEST -> {
                                            LogHelper.e(WSClientProcessor.TAG, "WSE_TEST wsEvent=$wsEvent")
                                        }
                                        else -> {
                                            WSEventProcessor.process(wsEvent)
                                        }

                                    }


                                    /**
                                     * 连接成功
                                     */


                                } catch (e: Exception) {
                                    e.printStackTrace()
                                    LogHelper.e(TAG, "client handle error===>${e.message}")
                                }

                            }
                            else -> {
                                LogHelper.e(TAG, "client type error===>${it}")
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                LogHelper.e(TAG, "client connect error===>${e.message}")
                callBack(CONNECT_FAIL, HostInfo(e.message!!, 0f, 0f))
            }

        }
    }

    suspend fun close() {
        try {
            send(
                WSEvent(
                    WSMode.CLIENT,
                    WSEType.WSE_CLOSE,
                    mutableMapOf("command" to "bye"),
                    null
                )
            )
            clientWebSocketSession?.close()
            DoKitManager.WS_MODE = WSMode.UNKNOW
            DokitMcConnectManager.currentClientHistory = null
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }


    fun send(wsEvent: WSEvent) {
        clientWebSocketSession?.let {
            CoroutineScope(it.coroutineContext).launch {
                if (it.isActive) {
                    it.outgoing.send(Frame.Text(GsonUtils.toJson(wsEvent)))
                } else {
                    LogHelper.e(TAG, "send() clientWebSocketSession is not active.")
                }
            }
        }
    }

    fun session(): DefaultClientWebSocketSession? {
        return clientWebSocketSession;
    }


}




