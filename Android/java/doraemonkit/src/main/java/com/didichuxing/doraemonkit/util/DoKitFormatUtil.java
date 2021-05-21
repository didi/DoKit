package com.didichuxing.doraemonkit.util;

import java.util.Date;

/**
 * Created by wanglikun on 2019-06-12
 */
public class DoKitFormatUtil {
    private DoKitFormatUtil() {
    }

    public static String format(long time) {
        return new Date(time).toString();
    }
}