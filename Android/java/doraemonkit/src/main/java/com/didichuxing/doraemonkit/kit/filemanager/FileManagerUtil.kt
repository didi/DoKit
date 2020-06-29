package com.didichuxing.doraemonkit.kit.filemanager

import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.PathUtils

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
    private val internalAppRootPath by lazy { PathUtils.getInternalAppDataPath() }
    private val internalAppRootReplacePath by lazy { FileUtils.getFileName(PathUtils.getInternalAppDataPath()) }
    private val externalStorageRootPath by lazy { PathUtils.getExternalStoragePath() }
    private val externalStorageRootReplacePath by lazy { "external" }


    /**
     * 输出相对路径
     */
    fun relativeRootPath(path: String?): String {
        path?.let {
            if (it.contains(internalAppRootPath)) {
                return it.replace(internalAppRootPath, internalAppRootReplacePath)
            } else if (it.contains(externalStorageRootPath)) {
                return it.replace(externalStorageRootPath, externalStorageRootReplacePath)
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
            if (it.contains(internalAppRootReplacePath)) {
                return it.replace(internalAppRootReplacePath, internalAppRootPath)
            } else if (it.contains(externalStorageRootReplacePath)) {
                return it.replace(externalStorageRootReplacePath, externalStorageRootPath)
            }
            return it
        }
        return ""
    }

}