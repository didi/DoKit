package com.didichuxing.doraemonkit.kit.filemanager.sqlite

import android.content.ContentValues
import android.database.Cursor
import com.didichuxing.doraemonkit.DoKit
import com.didichuxing.doraemonkit.kit.core.DoKitManager
import com.didichuxing.doraemonkit.kit.filemanager.sqlite.bean.RowFiledInfo
import com.didichuxing.doraemonkit.kit.filemanager.sqlite.bean.TableFieldInfo
import com.didichuxing.doraemonkit.kit.filemanager.sqlite.dao.SQLiteDB
import com.didichuxing.doraemonkit.kit.filemanager.sqlite.factory.DBFactory
import com.didichuxing.doraemonkit.kit.filemanager.sqlite.factory.EncryptDBFactory
import com.didichuxing.doraemonkit.kit.filemanager.sqlite.factory.NormalDBFactory
import com.didichuxing.doraemonkit.kit.filemanager.sqlite.util.DBUtil

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020/6/24-17:23
 * 描    述：
 * 修订历史：
 * ================================================
 */
object DBManager {
    val TAG = "DBHelper"
    private val sqliteDBs: MutableMap<String, SQLiteDB> = mutableMapOf()

    private fun openDB(databasePath: String, databaseName: String): SQLiteDB? {
        var dbFactory: DBFactory = NormalDBFactory()
        val password: String?
        if (DoKitManager.DATABASE_PASS.isEmpty()) {
            password = null
        } else {
            password = DoKitManager.DATABASE_PASS[databaseName]
            password?.let {
                dbFactory = EncryptDBFactory()
            }
        }

        return if (sqliteDBs.containsKey(databasePath)) {
            sqliteDBs["databasePath"]
        } else {
            sqliteDBs["databasePath"] = dbFactory.create(DoKit.APPLICATION.applicationContext, databasePath, password)
            sqliteDBs["databasePath"]
        }

    }

    /**
     * 获取所有的表名
     */
    fun getAllTableName(databasePath: String, databaseName: String): List<String> {
        val openDB = openDB(databasePath, databaseName)
        val tables = mutableListOf<String>()
        openDB?.let { db ->
            val cursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table' OR type='view' ORDER BY name COLLATE NOCASE", null)
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
        }

        return tables
    }

    /**
     * 获取表中的数据
     */
    fun getTableData(databasePath: String, databaseName: String, tableName: String): Map<String, Any> {
        val openDB = openDB(databasePath, databaseName)
        val params = mutableMapOf<String, Any>()
        openDB?.let { db ->
            val tableFieldInfos = getTableFieldInfos(db, tableName)
            val tableRows = getTableRows(db, tableName, tableFieldInfos)
            params["fieldInfo"] = tableFieldInfos
            params["rows"] = tableRows
        }
        return params
    }


    /**
     * 插入数据
     */
    fun insertRow(databasePath: String, databaseName: String, tableName: String, rowDatas: List<RowFiledInfo>): Long {
        val openDB = openDB(databasePath, databaseName)
        if (rowDatas.isEmpty()) {
            return -1
        }
        openDB?.let { db ->
            val contentValues = ContentValues()
            rowDatas.forEach { rowInfo ->
                if (rowInfo.value.isNullOrBlank()) {
                    contentValues.put(rowInfo.title, "null")
                } else {
                    contentValues.put(rowInfo.title, rowInfo.value)
                }
            }
            return db.insert("[$tableName]", null, contentValues)
        }

        return -1

    }


    /**
     * 更新数据
     */
    fun updateRow(databasePath: String, databaseName: String, tableName: String, rowDatas: List<RowFiledInfo>): Int {
        val openDB = openDB(databasePath, databaseName)
        if (rowDatas.isEmpty()) {
            return -1
        }

        openDB?.let { db ->
            val contentValues = ContentValues()
            var whereClause = ""
            val whereArgList = mutableListOf<String>()
            rowDatas.forEach { rowInfo ->
                if (rowInfo.isPrimary) {
                    if (whereClause.isBlank()) {
                        whereClause = "${rowInfo.title} = ? "
                    } else {
                        whereClause = "$whereClause and ${rowInfo.title} = ? "
                    }
                    whereArgList.add(if (rowInfo.value.isNullOrBlank()) {
                        "null"
                    } else {
                        rowInfo.value
                    })
                } else {
                    if (rowInfo.value.isNullOrBlank()) {
                        contentValues.put(rowInfo.title, "null")
                    } else {
                        contentValues.put(rowInfo.title, rowInfo.value)
                    }
                }
            }
            val whereArgs = whereArgList.toTypedArray()
            return db.update("[$tableName]", contentValues, whereClause, whereArgs)

        }


        return -1
    }

    /**
     * 删除数据
     */
    fun deleteRow(databasePath: String, databaseName: String, tableName: String, rowDatas: List<RowFiledInfo>): Int {
        val openDB = openDB(databasePath, databaseName)
        if (rowDatas.isEmpty()) {
            return -1
        }

        openDB?.let { db ->
            var whereClause = ""
            val whereArgList = mutableListOf<String>()
            rowDatas.forEach { rowInfo ->
                if (rowInfo.isPrimary) {
                    if (whereClause.isBlank()) {
                        whereClause = "${rowInfo.title} = ? "
                    } else {
                        whereClause = "$whereClause and ${rowInfo.title} = ? "
                    }
                    whereArgList.add(if (rowInfo.value.isNullOrBlank()) {
                        "null"
                    } else {
                        rowInfo.value
                    })
                }
            }
            val whereArgs = whereArgList.toTypedArray()
            return db.delete("[$tableName]", whereClause, whereArgs)
        }

        return -1

    }

    /**
     * 获取指定表中所有的字段
     */
    private fun getTableFieldInfos(openDB: SQLiteDB, tableName: String): List<TableFieldInfo> {
        val tableFields = mutableListOf<TableFieldInfo>()
        val pragmaQuery = "PRAGMA table_info([$tableName])"
        val cursor = openDB.rawQuery(pragmaQuery, null)
        cursor?.let {
            if (it.count > 0) {
                it.moveToFirst()
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

    /**
     * 获取表中的所有数据
     */
    private fun getTableRows(sqLiteDB: SQLiteDB, tableName: String, tableFieldInfos: List<TableFieldInfo>): MutableList<MutableMap<String, String?>> {
        val cursor = sqLiteDB.rawQuery("SELECT * FROM  $tableName", null)
        val rows = mutableListOf<MutableMap<String, String?>>()
        cursor?.let {
            if (it.count > 0) {
                it.moveToFirst()
                do {
                    val row = mutableMapOf<String, String?>()
                    for (index in 0 until it.columnCount) {
                        when (it.getType(index)) {
                            Cursor.FIELD_TYPE_BLOB -> row[tableFieldInfos[index].title] = DBUtil.blob2String(cursor.getBlob(index))
                            Cursor.FIELD_TYPE_FLOAT -> row[tableFieldInfos[index].title] = cursor.getDouble(index).toString()
                            Cursor.FIELD_TYPE_INTEGER -> row[tableFieldInfos[index].title] = cursor.getLong(index).toString()
                            Cursor.FIELD_TYPE_STRING -> row[tableFieldInfos[index].title] = cursor.getString(index)
                            else -> row[tableFieldInfos[index].title] = cursor.getString(index)
                        }
                    }
                    rows.add(row)
                } while (it.moveToNext())
            }
            it.close()
        }
        return rows
    }


}