package com.didichuxing.doraemondemo.mc;

import java.io.File;
import java.io.InputStream;

/**
 * didi Create on 2022/3/17 .
 * <p>
 * Copyright (c) 2022/3/17 by didiglobal.com.
 *
 * @author <a href="realonlyone@126.com">zhangjun</a>
 * @version 1.0
 * @Date 2022/3/17 4:54 下午
 * @Description 用一句话说明文件功能
 */

public class FileUtils {

    /**
     * 获取assets目录下的单个文件
     * 这种方式只能用于webview加载
     * 读取文件夹，直接取路径是不行的
     *
     * @param fileName 文件夹名
     * @return File
     */
    public static File getFileFromAssetsFile(String fileName) {
        String path = "file:///android_asset/" + fileName;
        File file = new File(path);
        return file;
    }

    public static String readString(InputStream inputStream) throws Exception {
        int size = inputStream.available();
        byte[] buffer = new byte[size];
        inputStream.read(buffer, 0, size);
        return new String(buffer);
    }
}
