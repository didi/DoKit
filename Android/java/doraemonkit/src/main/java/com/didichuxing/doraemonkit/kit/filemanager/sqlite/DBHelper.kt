package com.didichuxing.doraemonkit.kit.filemanager.sqlite

import android.database.Cursor
import com.didichuxing.doraemonkit.DoraemonKit
import com.didichuxing.doraemonkit.kit.filemanager.sqlite.bean.TableFieldInfo
import com.didichuxing.doraemonkit.kit.filemanager.sqlite.dao.SQLiteDB
import com.didichuxing.doraemonkit.kit.filemanager.sqlite.factory.DBFactory
import com.didichuxing.doraemonkit.kit.filemanager.sqlite.factory.NormalDBFactory
import com.didichuxing.doraemonkit.kit.filemanager.sqlite.util.DBUtil
import com.didichuxing.doraemonkit.util.LogHelper

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020/6/24-17:23
 * 描    述：
 * 修订历史：
 * ================================================
 */
object DBHelper {
    val TAG = "DBHelper"
    private val sqliteDBs: MutableMap<String, SQLiteDB> = mutableMapOf()


    private fun openDB(dbFactory: DBFactory, databasePath: String, password: String?): SQLiteDB {
        return if (sqliteDBs.containsKey(databasePath)) {
            sqliteDBs["databasePath"]!!
        } else {
            sqliteDBs["databasePath"] = dbFactory.create(DoraemonKit.APPLICATION!!.applicationContext, databasePath, password)
            sqliteDBs["databasePath"]!!
        }

    }

    /**
     * 获取所有的表名
     */
    fun getAllTableName(databasePath: String, password: String?): List<String> {
        val openDB = openDB(NormalDBFactory(), databasePath, password)
        val tables = mutableListOf<String>()
        val cursor = openDB.rawQuery("SELECT name FROM sqlite_master WHERE type='table' OR type='view' ORDER BY name COLLATE NOCASE", null)
        cursor?.let {
            if (it.moveToFirst()) {
                while (!it.isAfterLast) {
                    val name = it.getString(0)
                    tables.add(name)
                    it.moveToNext()
                }
            }
            it.close()
        }

        return tables
    }

    fun getTableData(databasePath: String, password: String?, tableName: String): Map<String, Any> {
        val openDB = openDB(NormalDBFactory(), databasePath, password)
        val tableFieldInfos = getTableFieldInfos(openDB, tableName)
        val tableRows = getTableRows(openDB, tableName)
        val params = mutableMapOf<String, Any>()
        params["fieldInfo"] = tableFieldInfos
        params["rows"] = tableRows
        return params
    }

    /**
     * 获取指定表中所有的字段
     */
    private fun getTableFieldInfos(openDB: SQLiteDB, tableName: String): List<TableFieldInfo> {
        val tableFields = mutableListOf<TableFieldInfo>()
        val pragmaQuery = "PRAGMA table_info([$tableName])"
        val cursor = openDB.rawQuery(pragmaQuery, null)
        cursor?.let {
            it.moveToFirst()
            if (it.count > 0) {
                do {
                    val tableFieldInfo = TableFieldInfo("", false)
                    for (index in 0 until it.columnCount) {
                        val columnName = it.getColumnName(index)
                        when (columnName) {
                            "name" -> tableFieldInfo.title = it.getString(index)
                            "pk" -> tableFieldInfo.isPrimary = cursor.getInt(index) == 1
                        }
                    }
                    tableFields.add(tableFieldInfo)
                } while (it.moveToNext())
            }

            it.close()
        }

        return tableFields
    }

    private fun getTableRows(sqLiteDB: SQLiteDB, tableName: String): MutableList<MutableList<String?>> {
        val cursor = sqLiteDB.rawQuery("SELECT * FROM  $tableName", null)
        val rows = mutableListOf<MutableList<String?>>()
        cursor?.let {
            it.moveToFirst()
            do {
                val row = mutableListOf<String?>()
                for (index in 0 until it.columnCount) {
                    when (it.getType(index)) {
                        Cursor.FIELD_TYPE_BLOB -> row.add(DBUtil.blob2String(cursor.getBlob(index)))
                        Cursor.FIELD_TYPE_FLOAT -> row.add(cursor.getDouble(index).toString())
                        Cursor.FIELD_TYPE_INTEGER -> row.add(cursor.getLong(index).toString())
                        Cursor.FIELD_TYPE_STRING -> row.add(cursor.getString(index))
                        else -> row.add(cursor.getString(index))
                    }
                }
                rows.add(row)
            } while (it.moveToNext())
            it.close()
        }
        return rows

    }


}