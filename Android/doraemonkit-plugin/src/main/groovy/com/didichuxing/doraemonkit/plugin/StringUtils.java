package com.didichuxing.doraemonkit.plugin;

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2019-12-19-11:24
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class StringUtils {
    /**
     * 判断是否是空字符串
     *
     * @param str 字符串
     * @return 是否为空
     */
    public static boolean isEmpty(String str) {
        if (str == null || str.equals("")) {
            return true;
        }
        return false;
    }
}
