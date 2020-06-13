package com.didichuxing.doraemonkit.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.text.format.Formatter
import androidx.core.content.FileProvider
import java.io.File
import java.util.*

/**
 * Created by luhongyan on 2020/06/13.
 */
object FileUtil {
    private const val TAG = "FileUtil"
    const val TXT = "txt"
    const val JPG = "jpg"
    const val DB = "db"
    const val SHARED_PREFS = "shared_prefs"
    const val XML = ".xml"
    fun getFileSize(context: Context?, file: File): String? {
        return if (!file.exists() || !file.isFile) {
            null
        } else Formatter.formatFileSize(context, file.length())
    }

    @JvmStatic
    fun getSuffix(file: File?): String {
        return if (file == null || !file.exists()) {
            ""
        } else file.name
                .substring(file.name.lastIndexOf(".") + 1)
                .toLowerCase(Locale.getDefault())
    }

    @JvmStatic
    fun systemShare(context: Context, file: File?) {
        if (file == null || !file.exists()) {
            return
        }
        val intent = Intent(Intent.ACTION_SEND)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        val uri: Uri
        try {
            uri = FileProvider.getUriForFile(context, context.packageName + ".debugfileprovider", file)
            val type = context.contentResolver.getType(uri)
            intent.setDataAndType(uri, type)
            intent.putExtra(Intent.EXTRA_STREAM, uri)
            if (intent.resolveActivity(context.packageManager) == null) {
                intent.setDataAndType(uri, "*/*")
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            LogHelper.e(TAG,
                    "The selected file can't be shared: $file")
        }
    }

    @JvmStatic
    fun isImage(file: File?): Boolean {
        if (file == null) {
            return false
        }
        val suffix = getSuffix(file)
        return "jpg" == suffix || "jpeg" == suffix || "png" == suffix || "bmp" == suffix
    }

    @JvmStatic
    fun isVideo(file: File?): Boolean {
        if (file == null) {
            return false
        }
        val suffix = getSuffix(file)
        return "3gp" == suffix || "mp4" == suffix || "mkv" == suffix || "webm" == suffix
    }

    @JvmStatic
    fun isDB(file: File?): Boolean {
        if (file == null) {
            return false
        }
        val suffix = getSuffix(file)
        return "db" == suffix
    }

    @JvmStatic
    fun isSp(file: File): Boolean {
        val parentFile = file.parentFile
        return if (parentFile != null && parentFile.name == SHARED_PREFS && file.name.contains(XML)) {
            true
        } else false
    }

    /**
     * @param file
     */
    @JvmStatic
    fun deleteDirectory(file: File) {
        if (file.isDirectory) {
            val listFiles = file.listFiles()
            for (f in listFiles) {
                deleteDirectory(f)
            }
            file.delete()
        } else {
            file.delete()
        }
    }

    @JvmStatic
    fun getDirectorySize(directory: File): Long {
        var size: Long = 0
        val listFiles = directory.listFiles() ?: return size
        for (file in listFiles) {
            size += if (file.isDirectory) {
                getDirectorySize(file)
            } else {
                file.length()
            }
        }
        return size
    }
}