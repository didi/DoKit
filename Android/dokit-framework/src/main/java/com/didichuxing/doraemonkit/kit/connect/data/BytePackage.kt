package com.didichuxing.doraemonkit.kit.connect.data


/**
 * didi Create on 2022/2/16 .
 *
 * Copyright (c) 2022/2/16 by didiglobal.com.
 *
 * @author <a href="realonlyone@126.com">zhangjun</a>
 * @version 1.0
 * @Date 2022/2/16 5:09 下午
 * @Description  byte类型的通信数据包
 */
data class BytePackage(
    val sign: String = "DoKit",
    val headLength: Int = -1,
    val dataLength: Long = -1,
    val textPackage: TextPackage,
    val data: ByteArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BytePackage

        if (sign != other.sign) return false
        if (headLength != other.headLength) return false
        if (dataLength != other.dataLength) return false
        if (textPackage != other.textPackage) return false
        if (!data.contentEquals(other.data)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = sign.hashCode()
        result = 31 * result + headLength
        result = 31 * result + dataLength.toInt()
        result = 31 * result + textPackage.hashCode()
        result = 31 * result + data.contentHashCode()
        return result
    }
}
