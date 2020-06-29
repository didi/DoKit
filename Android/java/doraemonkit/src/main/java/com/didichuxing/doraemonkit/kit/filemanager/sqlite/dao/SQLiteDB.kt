package com.didichuxing.doraemonkit.kit.filemanager.sqlite.dao

import android.content.ContentValues
import android.database.Cursor

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020/6/24-15:39
 * 描    述：数据库相关接口
 * 修订历史：
 * ================================================
 */
interface SQLiteDB {
    fun delete(table: String, whereClause: String, whereArgs: Array<String>): Int
    fun isOpen(): Boolean
    fun close()
    fun rawQuery(sql: String, selectionArgs: Array<String>?): Cursor?
    fun execSQL(sql: String)
    fun insert(table: String, nullColumnHack: String?, values: ContentValues): Long
    fun update(table: String, values: ContentValues, whereClause: String, whereArgs: Array<String>): Int
    fun getVersion(): Int
}