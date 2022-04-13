package com.didichuxing.doraemonkit.kit.mc.net

import android.os.Build
import androidx.annotation.RequiresApi
import com.didichuxing.doraemonkit.kit.test.event.ControlEvent
import com.didichuxing.doraemonkit.kit.test.event.EventType
import com.didichuxing.doraemonkit.kit.test.mock.data.HostInfo
import com.didichuxing.doraemonkit.kit.mc.utils.WSPackageUtils
import com.didichuxing.doraemonkit.util.*
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.cio.websocket.*
import io.ktor.routing.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.consumeEach
import java.time.Duration


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
            DoKitMcHostServer.wsSessionMaps[deviceModels] = this
            ToastUtils.showShort("从机【$deviceModels】已连接")
            val hostInfo = HostInfo(
                "${DeviceUtils.getManufacturer()}-${DeviceUtils.getModel()}",
                ScreenUtils.getAppScreenWidth().toFloat(),
                ScreenUtils.getAppScreenHeight().toFloat()
            )
            val wsEvent = ControlEvent(
                "",
                EventType.WSE_CONNECTED,
                mutableMapOf(
                    "hostInfo" to GsonUtils.toJson(hostInfo)
                ),
                null
            )

            outgoing.send(Frame.Text(WSPackageUtils.toPackageJson(wsEvent)))
            /**
             * 避免ws在收到第一条消息以后 通道自动关闭的问题
             * https://github.com/ktorio/ktor/issues/402
             */
            incoming.consumeEach {
                when (it) {
                    is Frame.Text -> {
                        try {
                            val wsEvent = WSPackageUtils.jsonToEvent(it.readText())
                            if (wsEvent.eventType == EventType.WSE_CLOSE) {
                                DoKitMcHostServer.send(
                                    ControlEvent(
                                        "",
                                        EventType.WSE_CLOSE,
                                        mutableMapOf(
                                            "command" to "confirmed bye"
                                        ),
                                        null
                                    )
                                )
                                close(CloseReason(CloseReason.Codes.NORMAL, "Client said BYE"))
                                ToastUtils.showShort("从机【$deviceModels】已断开连接")
                            } else {
                                WSServerProcessor.process(wsEvent)
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
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





