package com.didichuxing.doraemonkit.kit.filemanager.sqlite

import com.amitshekhar.sqlite.SQLiteDB

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
    var sqliteDB: SQLiteDB? = null
    var isDbOpened: Boolean = false


    fun openDB(database: String) {
        closeDB()

    }

    fun closeDB() {
        sqliteDB?.let {
            if (it.isOpen) {
                it.close()
                sqliteDB = null
                isDbOpened = false
            }
        }

    }
}