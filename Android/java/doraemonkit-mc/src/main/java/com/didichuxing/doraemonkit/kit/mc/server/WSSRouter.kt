package com.didichuxing.doraemonkit.kit.mc.server

import android.os.Build
import androidx.annotation.RequiresApi
import com.blankj.utilcode.util.DeviceUtils
import com.blankj.utilcode.util.GsonUtils
import com.blankj.utilcode.util.ScreenUtils
import com.blankj.utilcode.util.ToastUtils
import com.didichuxing.doraemonkit.kit.mc.all.*
import com.didichuxing.doraemonkit.kit.mc.client.DoKitWsClient
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.cio.websocket.*
import io.ktor.routing.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.consumeEach
import java.time.Duration
import kotlin.collections.set


/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020/6/23-14:35
 * 描    述：
 * 修订历史：
 * ================================================
 */
@RequiresApi(Build.VERSION_CODES.O)
val WSRouter: Application.() -> Unit = {
    install(DefaultHeaders)
    install(CallLogging)

    install(WebSockets) {
        pingPeriod = Duration.ofSeconds(60)
        timeout = Duration.ofSeconds(15)
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }

    routing {
        //一机多控
        webSocket("/mc") {

            val headers = this.call.request.headers
            val deviceModels = headers["deviceModel"]
            //保存
            DoKitWsServer.wsSessionMaps[deviceModels] = this
            ToastUtils.showShort("从机【$deviceModels】已连接")
            val hostInfo = HostInfo(
                "${DeviceUtils.getManufacturer()}-${DeviceUtils.getModel()}",
                ScreenUtils.getAppScreenWidth().toFloat(),
                ScreenUtils.getAppScreenHeight().toFloat()
            )
            val wsEvent = WSEvent(
                WSMode.HOST,
                WSEType.WSE_CONNECTED,
                mutableMapOf(
                    "hostInfo" to GsonUtils.toJson(hostInfo)
                ),
                null
            )
            outgoing.send(Frame.Text(GsonUtils.toJson(wsEvent)))
            /**
             * 避免ws在收到第一条消息以后 通道自动关闭的问题
             * https://github.com/ktorio/ktor/issues/402
             */
            incoming.consumeEach {
                when (it) {
                    is Frame.Text -> {
                        val wsEvent =
                            GsonUtils.fromJson<WSEvent>(it.readText(), WSEvent::class.java)
                        if (wsEvent.eventType == WSEType.WSE_CLOSE) {
                            DoKitWsServer.send(
                                WSEvent(
                                    WSMode.HOST,
                                    WSEType.WSE_CLOSE,
                                    mutableMapOf(
                                        "command" to "confirmed bye"
                                    ),
                                    null
                                )
                            )
                            close(CloseReason(CloseReason.Codes.NORMAL, "Client said BYE"))
                            ToastUtils.showShort("从机【$deviceModels】已断开连接")
                        } else {
                            WSEventProcessor.process(wsEvent)
                        }
                    }
                    else -> {

                    }
                }
            }
        }
    }
}

const val TAG = "WSRouter"





