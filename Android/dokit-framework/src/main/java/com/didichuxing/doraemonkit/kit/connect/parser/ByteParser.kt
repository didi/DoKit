package com.didichuxing.doraemonkit.kit.connect.parser

import com.didichuxing.doraemonkit.kit.connect.data.BytePackage
import com.didichuxing.doraemonkit.kit.connect.data.TextPackage
import okio.ByteString
import java.nio.ByteBuffer

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

object ByteParser {

    fun parse(bytes: ByteString): BytePackage {
        val buffer = bytes.asByteBuffer()
        val headLength = buffer.getInt(5)
        val dataLength = buffer.getLong(9)
        val headBytes = ByteArray(headLength)
        val dataBytes = ByteArray(dataLength.toInt())
        buffer.position(17)
        buffer.get(headBytes)
        buffer.get(dataBytes)

        val headText = String(headBytes)
        val textPackage = JsonParser.toTextPackage(headText)
        return BytePackage(
            headLength = headLength,
            dataLength = dataLength,
            textPackage = textPackage,
            data = dataBytes
        )
    }

    fun toByteString(textPackage: TextPackage, data: ByteArray): ByteString {
        return toByteString(BytePackage(textPackage = textPackage, data = data))
    }

    fun toByteString(bytePackage: BytePackage): ByteString {

        val headBytes: ByteArray = JsonParser.toJson(bytePackage.textPackage).toByteArray()
        val dataBytes: ByteArray = bytePackage.data
        val headLength = headBytes.size
        val dataLength = dataBytes.size

        val size = 17 + headLength + dataLength
        val buffer = ByteBuffer.allocate(size)

        buffer.put("DoKit".toByteArray())
        buffer.putInt(headLength)
        buffer.putLong(dataLength.toLong())
        buffer.put(headBytes)
        buffer.put(dataBytes)
        buffer.rewind()
        return ByteString.of(buffer)
    }
}
