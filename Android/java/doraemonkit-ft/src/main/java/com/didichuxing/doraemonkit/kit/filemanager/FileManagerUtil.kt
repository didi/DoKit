package com.didichuxing.doraemonkit.kit.filemanager

import com.didichuxing.doraemonkit.util.FileUtils
import com.didichuxing.doraemonkit.util.PathUtils

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020/6/29-17:10
 * 描    述：
 * 修订历史：
 * ================================================
 */
object FileManagerUtil {
    /**
     * 根目录匹配字段
     */
    const val ROOT_PATH_STR = "/root/"

    private val internalAppRootPath by lazy { PathUtils.getInternalAppDataPath() }
    private val internalAppRootReplacePath by lazy { FileUtils.getFileName(PathUtils.getInternalAppDataPath()) }
    public val externalStorageRootPath by lazy { PathUtils.getExternalStoragePath() }
    private val externalStorageRootReplacePath by lazy { "external" }


    /**
     * 输出相对路径
     */
    fun relativeRootPath(path: String?): String {
        path?.let {
            if (it.contains(internalAppRootPath)) {
                return it.replace(internalAppRootPath, "$ROOT_PATH_STR$internalAppRootReplacePath")
            } else if (it.contains(externalStorageRootPath)) {
                return it.replace(externalStorageRootPath, "$ROOT_PATH_STR$externalStorageRootReplacePath")
            }
            return it
        }

        return ""
    }

    /**
     * 合并绝对路径
     */
    fun absoluteRootPath(path: String?): String {
        path?.let {
            if (it.contains("$ROOT_PATH_STR$internalAppRootReplacePath")) {
                return it.replace("$ROOT_PATH_STR$internalAppRootReplacePath", internalAppRootPath)
            } else if (it.contains("$ROOT_PATH_STR$externalStorageRootReplacePath")) {
                return it.replace("$ROOT_PATH_STR$externalStorageRootReplacePath", externalStorageRootPath)
            }
            return it
        }
        return ""
    }

}