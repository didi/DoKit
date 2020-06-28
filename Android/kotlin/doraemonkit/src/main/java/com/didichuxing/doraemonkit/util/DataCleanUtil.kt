package com.didichuxing.doraemonkit.util

import android.content.Context
import android.os.Environment
import android.text.format.Formatter
import java.io.File

/**
 * Created by luhongyan on 2020/06/13.
 */
object DataCleanUtil {
    /**
     * @param context
     */
    fun cleanInternalCache(context: Context) {
        FileUtil.deleteDirectory(context.cacheDir)
    }

    /**
     * @param context
     */
    fun cleanDatabases(context: Context) {
        FileUtil.deleteDirectory(File(context.filesDir.parent + "/databases"))
    }

    /**
     * @param context
     */
    fun cleanSharedPreference(context: Context) {
        FileUtil.deleteDirectory(File(context.filesDir.parent + "/shared_prefs"))
    }

    /**
     * @param context
     */
    fun cleanFiles(context: Context) {
        FileUtil.deleteDirectory(context.filesDir)
    }

    /**
     * @param context
     */
    fun cleanExternalCache(context: Context) {
        if (Environment.getExternalStorageState() ==
                Environment.MEDIA_MOUNTED) {
            FileUtil.deleteDirectory(context.externalCacheDir!!)
        }
    }

    /**
     * @param filePath
     */
    fun cleanCustomCache(filePath: String?) {
        FileUtil.deleteDirectory(File(filePath))
    }

    /**
     * clean app data
     *
     * @param context
     * @param filepath
     */
    @JvmStatic
    fun cleanApplicationData(context: Context, vararg filepath: String?) {
        cleanInternalCache(context)
        cleanExternalCache(context)
        cleanDatabases(context)
        cleanSharedPreference(context)
        cleanFiles(context)
        if (filepath == null) {
            return
        }
        for (filePath in filepath) {
            cleanCustomCache(filePath)
        }
    }

    fun getApplicationDataSize(context: Context): Long {
        var size: Long = 0
        // internal cache
        size += FileUtil.getDirectorySize(context.cacheDir)
        // databases
        size += FileUtil.getDirectorySize(File(context.filesDir.parent + "/databases"))
        // shared preference
        size += FileUtil.getDirectorySize(File(context.filesDir.parent + "/shared_prefs"))
        // files
        size += FileUtil.getDirectorySize(context.filesDir)
        return size
    }

    @JvmStatic
    fun getApplicationDataSizeStr(context: Context): String {
        return Formatter.formatFileSize(context, getApplicationDataSize(context))
    }
}