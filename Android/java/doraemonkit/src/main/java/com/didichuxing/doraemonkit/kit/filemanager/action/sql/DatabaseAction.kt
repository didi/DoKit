package com.didichuxing.doraemonkit.kit.filemanager.action.sql

import com.didichuxing.doraemonkit.kit.filemanager.sqlite.DBHelper

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020/6/28-11:36
 * 描    述：
 * 修订历史：
 * ================================================
 */
object DatabaseAction {
    fun allTablesRes(filePath: String): MutableMap<String, Any> {
        val response = mutableMapOf<String, Any>()
        val tables = DBHelper.getAllTableName(filePath, null)
        response["code"] = 200
        response["data"] = tables
        return response
    }

    fun tableDatasRes(filePath: String, tableName: String): MutableMap<String, Any> {
        val response = mutableMapOf<String, Any>()
        val tables = DBHelper.getTableData(filePath, null, tableName)
        response["code"] = 200
        response["data"] = tables
        return response
    }
}