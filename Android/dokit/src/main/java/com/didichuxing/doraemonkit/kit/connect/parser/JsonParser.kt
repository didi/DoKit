package com.didichuxing.doraemonkit.kit.connect.parser

import com.didichuxing.doraemonkit.kit.connect.ConnectConfig
import com.didichuxing.doraemonkit.kit.connect.data.LoginData
import com.didichuxing.doraemonkit.kit.connect.data.PackageType
import com.didichuxing.doraemonkit.kit.connect.data.TextPackage
import com.didichuxing.doraemonkit.util.GsonUtils
import com.didichuxing.doraemonkit.util.RandomUtils

object JsonParser {

    fun toTextPackage(text: String): TextPackage {
        return GsonUtils.fromJson<TextPackage>(text, TextPackage::class.java)
    }

    fun toLoginData(text: String): LoginData {
        return GsonUtils.fromJson<LoginData>(text, LoginData::class.java)
    }

    fun toJson(data: Any): String {
        return GsonUtils.toJson(data)
    }

    fun toJson(type: PackageType, data: Any): String {
        val text = toJson(data)
        val textPackage = TextPackage(
            RandomUtils.random32HexString(),
            type,
            text,
            channelSerial = ConnectConfig.getConnectSerial()
        )
        return toJson(textPackage)
    }

    fun toLoginJson(data: LoginData): String {
        return toJson(PackageType.LOGIN, data)
    }
}
