package com.didichuxing.doraemondemo.module.db

import com.didichuxing.doraemondemo.db.DatabaseHelper
import com.didichuxing.doraemonkit.util.Utils

object DataBaseTest {

    fun test() {
        val dbHelper = DatabaseHelper(Utils.getApp(), "BookStore.db", null, 1)
        dbHelper.writableDatabase
        dbHelper.close()
    }
}
