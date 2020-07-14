package com.didichuxing.doraemonkit.kit.filemanager.sqlite.bean

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020/6/29-14:31
 * 描    述：
 * 修订历史：
 * ================================================
 */
data class RowRequestInfo(val dirPath: String, val fileName: String, val tableName: String, val rowDatas: List<RowFiledInfo>)