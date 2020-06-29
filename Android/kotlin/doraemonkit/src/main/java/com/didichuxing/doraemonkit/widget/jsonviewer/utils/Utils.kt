package com.didichuxing.doraemonkit.widget.jsonviewer.utils

import java.util.regex.Pattern

/**
 * Created by yuyuhang on 2017/11/30.
 */
object Utils {
    private val urlPattern = Pattern.compile("^((https|http|ftp|rtsp|mms)?://)"
            + "?(([0-9a-z_!~*'().&=+$%-]+: )?[0-9a-z_!~*'().&=+$%-]+@)?" //ftp的user@
            + "(([0-9]{1,3}\\.){3}[0-9]{1,3}" // IP形式的URL- 199.194.52.184
            + "|" // 允许IP和DOMAIN（域名）
            + "([0-9a-z_!~*'()-]+\\.)*" // 域名- www.
            + "([0-9a-z][0-9a-z-]{0,61})?[0-9a-z]\\." // 二级域名
            + "[a-z]{2,6})" // first level domain- .com or .museum
            + "(:[0-9]{1,4})?" // 端口- :80
            + "((/?)|" // a slash isn't required if there is no file name
            + "(/[0-9a-z_!~*'().;?:@&=+$,%#-]+)+/?)$")

    /**
     * 判断字符串是否是url
     *
     * @param str
     * @return
     */
    @JvmStatic
    fun isUrl(str: String?): Boolean {
        return urlPattern.matcher(str).matches()
    }

    /**
     * json 格式化缩进(格式化前不能有缩进，最好是格式化从服务端下发的)
     *
     * @param jsonStr
     * @return
     */
    fun jsonFormat(jsonStr: String?): String {
        if (jsonStr == null) return ""
        var level = 0
        val builder = StringBuilder()
        for (i in 0 until jsonStr.length) {
            val c = jsonStr[i]
            if (level > 0 && '\n' == builder[builder.length - 1]) {
                builder.append(getHierarchyStr(level))
            }
            when (c) {
                '{', '[' -> {
                    builder.append(c).append("\n")
                    level++
                }
                ',' -> builder.append(c).append("\n")
                '}', ']' -> {
                    builder.append("\n")
                    level--
                    builder.append(getHierarchyStr(level))
                    builder.append(c)
                }
                else -> builder.append(c)
            }
        }
        return builder.toString()
    }

    /**
     * 对应层级前面所需的空格数
     *
     * @param hierarchy 缩进层级
     * @return
     */
    @JvmStatic
    fun getHierarchyStr(hierarchy: Int): String {
        val levelStr = StringBuilder()
        for (levelI in 0 until hierarchy) {
            levelStr.append("      ")
        }
        return levelStr.toString()
    }
}