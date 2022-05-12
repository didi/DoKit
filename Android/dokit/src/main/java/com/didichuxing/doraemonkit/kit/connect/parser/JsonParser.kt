package com.didichuxing.doraemonkit.kit.connect.parser

import com.didichuxing.doraemonkit.kit.connect.ConnectConfig
import com.didichuxing.doraemonkit.kit.connect.data.LoginData
import com.didichuxing.doraemonkit.kit.connect.data.PackageType
import com.didichuxing.doraemonkit.kit.connect.data.TextPackage
import com.didichuxing.doraemonkit.util.GsonUtils
import com.didichuxing.doraemonkit.util.RandomUtils

/**
 * didi Create on 2022/4/12 .
 *
 * Copyright (c) 2022/4/12 by didiglobal.com.
 *
 * @author <a href="realonlyone@126.com">zhangjun</a>
 * @version 1.0
 * @Date 2022/4/12 6:07 下午
 * @Description 用一句话说明文件功能
 */
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
        return toJson(type, data, "text")
    }

    fun toJson(type: PackageType, data: Any, contentType: String): String {
        val text = toJson(data)
        val textPackage = TextPackage(
            pid = RandomUtils.random32HexString(),
            type = type,
            data = text,
            contentType = contentType,
            connectSerial = ConnectConfig.getConnectSerial()
        )
        return toJson(textPackage)
    }

    fun toTextPackage(type: PackageType, data: Any, contentType: String): TextPackage {
        val text = toJson(data)
        return TextPackage(
            pid = RandomUtils.random32HexString(),
            type = type,
            data = text,
            contentType = contentType,
            connectSerial = ConnectConfig.getConnectSerial()
        )
    }

    fun toLoginJson(data: LoginData): String {
        return toJson(PackageType.LOGIN, data)
    }
}
