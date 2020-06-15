package com.didichuxing.doraemonkit.kit.fileexplorer

import com.didichuxing.doraemonkit.util.FileUtil
import java.io.File

/**
 * @author lostjobs created on 2020/6/14
 */
data class FileInfo(val file: File) {
    val isFile: Boolean = file.isFile
    val isDirectory: Boolean = file.isDirectory
    val isImage: Boolean = FileUtil.isImage(file)
    val isJPG: Boolean = FileUtil.isJPG(file)
    val isTxt: Boolean = FileUtil.isTxt(file)
    val isDB: Boolean = FileUtil.isDB(file)
    val isVideo: Boolean = FileUtil.isVideo(file)
    val isSp: Boolean = FileUtil.isSp(file)
}