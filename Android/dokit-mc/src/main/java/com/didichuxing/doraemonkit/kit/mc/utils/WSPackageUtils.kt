package com.didichuxing.doraemonkit.kit.mc.utils

import com.didichuxing.doraemonkit.kit.connect.DoKitConnectManager
import com.didichuxing.doraemonkit.kit.test.event.ControlEvent
import com.didichuxing.doraemonkit.kit.connect.data.PackageType
import com.didichuxing.doraemonkit.kit.connect.data.TextPackage
import com.didichuxing.doraemonkit.util.GsonUtils
import com.didichuxing.doraemonkit.util.RandomUtils

/**
 * 通信数据包处理
 */
object WSPackageUtils {

    fun toPackageJson(event: ControlEvent): String {
        return toPackageJson(RandomUtils.random32HexString(), PackageType.BROADCAST, event)
    }

    fun toPackageJson(id: String, type: PackageType, event: ControlEvent): String {
        val wsPackage = TextPackage(
            pid = id, type = type, data = GsonUtils.toJson(event),
            contentType = "action",
            connectSerial = DoKitConnectManager.getConnectSerial()
        )
        return GsonUtils.toJson(wsPackage)
    }

    fun toPackageJson(type: PackageType, data: String): String {
        val wsPackage = TextPackage(
            pid = RandomUtils.random32HexString(),
            type = type,
            data = data,
            connectSerial = DoKitConnectManager.getConnectSerial()
        )
        return GsonUtils.toJson(wsPackage)
    }

    fun toModePackageJson(type: PackageType, contentType: String): String {
        val wsPackage = TextPackage(
            pid = RandomUtils.random32HexString(),
            type = type,
            data = "",
            contentType = contentType,
            connectSerial = DoKitConnectManager.getConnectSerial()
        )
        return GsonUtils.toJson(wsPackage)
    }

    fun jsonToPackage(data: String): TextPackage {
        val wsPackage = GsonUtils.fromJson<TextPackage>(data, TextPackage::class.java)
        return wsPackage
    }

    fun jsonToEvent(data: String): ControlEvent {
        val wsPackage = jsonToPackage(data)
        val wsEvent = GsonUtils.fromJson<ControlEvent>(wsPackage.data, ControlEvent::class.java)
        return wsEvent
    }
}
