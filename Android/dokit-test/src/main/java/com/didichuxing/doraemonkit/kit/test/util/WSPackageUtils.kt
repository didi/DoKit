package com.didichuxing.doraemonkit.kit.test.util

import com.didichuxing.doraemonkit.connect.DokitConnectManager
import com.didichuxing.doraemonkit.kit.test.event.ControlEvent
import com.didichuxing.doraemonkit.connect.data.PackageType
import com.didichuxing.doraemonkit.connect.data.TextPackage
import com.didichuxing.doraemonkit.util.GsonUtils

/**
 * 通信数据包处理
 */
object WSPackageUtils {

    fun toPackageJson(event: ControlEvent): String {
        return toPackageJson("000", PackageType.BROADCAST, event)
    }

    fun toPackageJson(id: String, type: PackageType, event: ControlEvent): String {
        val wsPackage = TextPackage(id, type, GsonUtils.toJson(event), connectSerial = DokitConnectManager.getConnectSerial())
        return GsonUtils.toJson(wsPackage)
    }

    fun toPackageJson(id: String, type: PackageType, data: String): String {
        val wsPackage = TextPackage(id, type, data, connectSerial = DokitConnectManager.getConnectSerial())
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
