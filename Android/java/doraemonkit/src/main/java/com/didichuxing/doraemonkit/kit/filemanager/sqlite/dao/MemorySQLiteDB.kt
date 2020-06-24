package com.didichuxing.doraemonkit.kit.filemanager.sqlite.dao

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020/6/24-16:40
 * 描    述：
 * 修订历史：
 * ================================================
 */
class MemorySQLiteDB(private val database: SupportSQLiteDatabase) : SQLiteDB {

    override fun delete(table: String, whereClause: String, whereArgs: Array<String>) {
        database.delete(table, whereClause, whereArgs)
    }

    override fun isOpen(): Boolean {
        return database.isOpen
    }

    override fun close() {
        //database.close()
    }

    override fun rawQuery(sql: String, selectionArgs: Array<String>): Cursor {
        return database.query(sql, selectionArgs)
    }

    override fun execSQL(sql: String) {
        database.execSQL(sql)
    }

    override fun insert(table: String, nullColumnHack: String, values: ContentValues): Long {
        return database.insert(table, 0, values)
    }

    override fun update(table: String, values: ContentValues, whereClause: String, whereArgs: Array<String>): Int {
        return database.update(table, 0, values, whereClause, whereArgs)
    }

    override fun getVersion(): Int {
        return database.version
    }
}