package com.didichuxing.doraemonkit.util

import org.junit.Assert
import org.junit.Test
import java.util.*

/**
 * ================================================
 * 作    者：jaydroid（王学杰）
 * 版    本：1.0
 * 创建日期：2021/11/17-14:55
 * 描    述：
 * 修订历史：
 * ================================================
 */
class VersionUtilsTest {

    @Test
    fun testCompareVersion() {
        val v1 = VersionUtils.Version("1.1")
        val v2 = VersionUtils.Version("1.1.1")
        val result1 = v1.compareTo(v2) // return -1 (v1 < v2)
        Assert.assertEquals(-1, result1)

        val v3 = VersionUtils.Version("2.0")
        val v4 = VersionUtils.Version("1.9.9")
        val result2 = v3.compareTo(v4) // return -1 (v3 > v4)
        Assert.assertEquals(1, result2)

        val v5 = VersionUtils.Version("1.0")
        val v6 = VersionUtils.Version("1")
        val result3 = v5.compareTo(v6) // return 0 (v5 = v6)
        Assert.assertEquals(0, result3)

        val versions: MutableList<VersionUtils.Version> = ArrayList()
        versions.add(VersionUtils.Version("2"))
        versions.add(VersionUtils.Version("1.0.5"))
        versions.add(VersionUtils.Version("1.01.0"))
        versions.add(VersionUtils.Version("1.00.1"))
        val min = Collections.min(versions) // return min version
        val max = Collections.max(versions) // return max version
        Assert.assertEquals(VersionUtils.Version("1.00.1"), min)
        Assert.assertEquals(VersionUtils.Version("2"), max)

        // WARNING
        val v7 = VersionUtils.Version("2.06")
        val v8 = VersionUtils.Version("2.060")
        val result5 = v7 == v8 // return false
        Assert.assertFalse(result5)
    }
}
