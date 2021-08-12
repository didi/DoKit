package com.didichuxing.doraemonkit.util;

import java.io.File;

import android.content.Context;
import android.os.Environment;
import android.text.format.Formatter;

/**
 * Created by wanglikun on 2018/11/17.
 */
public class DataCleanUtil {
    private DataCleanUtil() {
    }

    /**
     * @param context
     */
    public static void cleanInternalCache(Context context) {
        DoKitFileUtil.deleteDirectory(context.getCacheDir());
    }

    /**
     * @param context
     */
    public static void cleanDatabases(Context context) {
        DoKitFileUtil.deleteDirectory(new File(context.getFilesDir().getParent() + "/databases"));
    }

    /**
     * @param context
     */
    public static void cleanSharedPreference(Context context) {
        DoKitFileUtil.deleteDirectory(new File(context.getFilesDir().getParent() + "/shared_prefs"));
    }

    /**
     * @param context
     */
    public static void cleanFiles(Context context) {
        DoKitFileUtil.deleteDirectory(context.getFilesDir());
    }

    /**
     * @param context
     */
    public static void cleanExternalCache(Context context) {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            DoKitFileUtil.deleteDirectory(context.getExternalCacheDir());
        }
    }

    /**
     * @param filePath
     */
    public static void cleanCustomCache(String filePath) {
        DoKitFileUtil.deleteDirectory(new File(filePath));
    }

    /**
     * clean app data
     *
     * @param context
     * @param filepath
     */
    public static void cleanApplicationData(Context context, String... filepath) {
        cleanInternalCache(context);
        cleanExternalCache(context);
        cleanDatabases(context);
        cleanSharedPreference(context);
        cleanFiles(context);
        if (filepath == null) {
            return;
        }
        for (String filePath : filepath) {
            cleanCustomCache(filePath);
        }
    }

    public static long getApplicationDataSize(Context context) {
//        long size = 0;
//        // internal cache
//        size += FileUtil.getDirectorySize(context.getCacheDir());
//        // databases
//        size += FileUtil.getDirectorySize(new File(context.getFilesDir().getParent() + "/databases"));
//        // shared preference
//        size += FileUtil.getDirectorySize(new File(context.getFilesDir().getParent() + "/shared_prefs"));
//        // files
//        size += FileUtil.getDirectorySize(context.getFilesDir());
        return DoKitFileUtil.getDirectorySize(new File(PathUtils.getInternalAppDataPath()));
    }

    public static String getApplicationDataSizeStr(Context context) {
        return Formatter.formatFileSize(context, getApplicationDataSize(context));
    }
}