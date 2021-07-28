package com.didichuxing.doraemonkit.kit.filemanager.action.sql

import android.database.sqlite.SQLiteDatabaseCorruptException
import com.didichuxing.doraemonkit.kit.filemanager.sqlite.DBManager
import com.didichuxing.doraemonkit.kit.filemanager.sqlite.bean.RowFiledInfo

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
    fun allTablesRes(filePath: String, fileName: String): MutableMap<String, Any> {
        val response = mutableMapOf<String, Any>()
        try {
            val tables = DBManager.getAllTableName(filePath, fileName)
            response["code"] = 200
            response["data"] = tables
        } catch (e: SQLiteDatabaseCorruptException) {
            response["code"] = 0
            response["data"] = arrayListOf<String>()
            response["message"] = "${e.message}"
        }

        return response
    }

    fun tableDatasRes(filePath: String, fileName: String, tableName: String): MutableMap<String, Any> {
        val response = mutableMapOf<String, Any>()
        val tables = DBManager.getTableData(filePath, fileName, tableName)
        response["code"] = 200
        response["data"] = tables
        return response
    }

    fun insertRowRes(filePath: String, fileName: String, tableName: String, rowDatas: List<RowFiledInfo>): Map<String, Any> {
        val response = mutableMapOf<String, Any>()
        val insertRow = DBManager.insertRow(filePath, fileName, tableName, rowDatas)
        if (insertRow <= 0) {
            response["code"] = 0
            response["success"] = false
            response["message"] = "insertRow=$insertRow"
        } else {
            response["code"] = 200
            response["success"] = true
            response["message"] = "insertRow=$insertRow"
        }
        return response
    }

    fun updateRowRes(filePath: String, fileName: String, tableName: String, rowDatas: List<RowFiledInfo>): Map<String, Any> {
        val response = mutableMapOf<String, Any>()
        val updateRow = DBManager.updateRow(filePath, fileName, tableName, rowDatas)
        if (updateRow <= 0) {
            response["code"] = 0
            response["success"] = false
            response["message"] = "updateRow=$updateRow"
        } else {
            response["code"] = 200
            response["success"] = true
            response["message"] = "updateRow=$updateRow"
        }
        return response

    }

    fun deleteRowRes(filePath: String, fileName: String, tableName: String, rowDatas: List<RowFiledInfo>): Map<String, Any> {
        val response = mutableMapOf<String, Any>()
        val deleteRow = DBManager.deleteRow(filePath, fileName, tableName, rowDatas)
        if (deleteRow <= 0) {
            response["code"] = 0
            response["success"] = false
            response["message"] = "deleteRow=$deleteRow"
        } else {
            response["code"] = 200
            response["success"] = true
            response["message"] = "deleteRow=$deleteRow"
        }
        return response
    }
}