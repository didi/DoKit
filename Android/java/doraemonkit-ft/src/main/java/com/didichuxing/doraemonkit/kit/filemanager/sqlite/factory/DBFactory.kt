package com.didichuxing.doraemonkit.kit.filemanager.sqlite.factory

import android.content.Context
import com.didichuxing.doraemonkit.kit.filemanager.sqlite.dao.SQLiteDB

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020/6/24-16:50
 * 描    述：
 * 修订历史：
 * ================================================
 */
interface DBFactory {
    fun create(context: Context, path: String, password: String?): SQLiteDB
}