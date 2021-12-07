package com.didichuxing.doraemonkit.util


/**
 * ================================================
 * 作    者：jaydroid（王学杰）
 * 版    本：1.0
 * 创建日期：2021/11/17-14:55
 * 描    述：
 * 修订历史：
 * ================================================
 */
object VersionUtils {

    /**
     * Compare two string version
     *
     * @param version1
     * @param version2
     * @return 0, 1, -1
     *
     *  0: version1 = version2
     *  1: version1 > version2
     * -1: version1 < version2
     */
    @JvmStatic
    fun compareVersion(version1: String, version2: String): Int {
        return Version(version1).compareTo(Version(version2))
    }

    class Version(val version: String) : Comparable<Version> {

        init {
            require(version.matches("[0-9]+(\\.[0-9]+)*".toRegex())) { "Invalid version format" }
        }

        override fun compareTo(other: Version): Int {
            if (other.version.isBlank()) return 1
            val thisParts = this.version.split(".").toTypedArray()
            val thatParts = other.version.split(".").toTypedArray()
            val length = thisParts.size.coerceAtLeast(thatParts.size)
            for (i in 0 until length) {
                val thisPart = if (i < thisParts.size) thisParts[i].toInt() else 0
                val thatPart = if (i < thatParts.size) thatParts[i].toInt() else 0
                if (thisPart < thatPart) return -1
                if (thisPart > thatPart) return 1
            }
            return 0
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other == null) return false
            return if (this.javaClass != other.javaClass) false
            else this.compareTo(other as Version) == 0
        }

        override fun hashCode(): Int {
            return version.hashCode()
        }


    }
}



