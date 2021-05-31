package com.didichuxing.doraemonkit.kit.filemanager.sqlite.factory

import android.content.Context
import com.didichuxing.doraemonkit.kit.filemanager.sqlite.dao.EncryptSQLiteDB
import com.didichuxing.doraemonkit.kit.filemanager.sqlite.dao.SQLiteDB
import com.tencent.wcdb.database.SQLiteCipherSpec
import com.tencent.wcdb.database.SQLiteDatabase


/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020/6/24-16:49
 * 描    述：
 * 修订历史：
 * ================================================
 */
class EncryptDBFactory : DBFactory {
    override fun create(context: Context, path: String, password: String?): SQLiteDB {
        return EncryptSQLiteDB(SQLiteDatabase.openOrCreateDatabase(path, password?.toByteArray(), null, null, 1))
    }
}