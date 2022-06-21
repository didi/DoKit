package com.didichuxing.doraemonkit.kit.connect

import android.graphics.Bitmap
import com.didichuxing.doraemonkit.kit.connect.data.PackageType
import com.didichuxing.doraemonkit.kit.connect.parser.ByteParser
import com.didichuxing.doraemonkit.kit.connect.parser.JsonParser
import com.didichuxing.doraemonkit.util.RandomUtils
import okio.ByteString
import org.junit.Test
import java.io.ByteArrayOutputStream


/**
 * didi Create on 2022/4/21 .
 *
 * Copyright (c) 2022/4/21 by didiglobal.com.
 *
 * @author <a href="realonlyone@126.com">zhangjun</a>
 * @version 1.0
 * @Date 2022/4/21 7:08 下午
 * @Description 用一句话说明文件功能
 */

class ByteParserTest {


    @Test
    fun test() {

        val bytes = "byteArray".toByteArray()

        val dataMap = mutableMapOf<String, String>()
        dataMap["caseId"] = RandomUtils.random32HexString()
        dataMap["image"] = "fileName"
        dataMap["type"] = "jpeg"

        val data = JsonParser.toJson(dataMap)
        val textPackage = JsonParser.toTextPackage(PackageType.AUTOTEST, data, "image")
        val byteString = ByteParser.toByteString(textPackage, bytes)

        ByteParser.parse(byteString)

    }

    fun getByteString(): ByteString {
        val bytes = "byteArray".toByteArray()

        val dataMap = mutableMapOf<String, String>()
        dataMap["caseId"] = RandomUtils.random32HexString()
        dataMap["image"] = "fileName"
        dataMap["type"] = "jpeg"

        val data = JsonParser.toJson(dataMap)
        val textPackage = JsonParser.toTextPackage(PackageType.AUTOTEST, data, "image")
        val byteString = ByteParser.toByteString(textPackage, bytes)
        return byteString
    }
}
