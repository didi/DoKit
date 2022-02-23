package com.didichuxing.doraemonkit.kit.mc.ability

import com.didichuxing.doraemonkit.constant.WSMode
import com.didichuxing.doraemonkit.kit.core.DoKitManager
import com.didichuxing.doraemonkit.kit.mc.all.ConnectMode
import com.didichuxing.doraemonkit.kit.mc.all.DokitMcConnectManager
import com.didichuxing.doraemonkit.kit.mc.report.MCRecordManager
import com.didichuxing.doraemonkit.kit.mc.net.DoKitMcConnectClient
import com.didichuxing.doraemonkit.kit.mc.net.DoKitMcHostServer
import com.didichuxing.doraemonkit.kit.mc.net.WSEvent

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020/11/13-15:38
 * 描    述：
 * 修订历史：
 * ================================================
 */
object DoKitMcEventDispatcher {

    fun send(wsEvent: WSEvent) {
        //拦截相关手势信息 用来做录制回放
        MCRecordManager.intercept(wsEvent)

        if (DokitMcConnectManager.connectMode == ConnectMode.CONNECT) {
            DoKitMcConnectClient.send(wsEvent)
        }

        if (DoKitManager.WS_MODE == WSMode.HOST) {
            DoKitMcHostServer.send(wsEvent)
        }

    }

}
