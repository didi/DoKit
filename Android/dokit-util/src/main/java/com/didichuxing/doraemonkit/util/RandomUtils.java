package com.didichuxing.doraemonkit.util;

import java.util.Random;

/**
 * didi Create on 2022/4/1 .
 * <p>
 * Copyright (c) 2022/4/1 by didiglobal.com.
 *
 * @author <a href="realonlyone@126.com">zhangjun</a>
 * @version 1.0
 * @Date 2022/4/1 4:59 下午
 * @Description 用一句话说明文件功能
 */

public class RandomUtils {

    public static final int RANDOM_MIN_LENGTH = 4;

    private RandomUtils() {
    }

    public static String random16HexString() {
        return randomHexString(16);
    }

    public static String random32HexString() {
        return randomHexString(32);
    }

    public static String random64HexString() {
        return randomHexString(64);
    }

    public static String random128HexString() {
        return randomHexString(128);
    }

    public static String random256HexString() {
        return randomHexString(256);
    }

    public static String randomHexString(int length) {
        Random random = new Random();
        if (length < RANDOM_MIN_LENGTH) {
            length = RANDOM_MIN_LENGTH;
        }
        byte[] buffer = new byte[length / 2];
        random.nextBytes(buffer);
        return ConvertUtils.bytes2HexString(buffer);
    }


}
