package com.didichuxing.doraemondemo.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteDatabase.CursorFactory
import android.database.sqlite.SQLiteOpenHelper
import android.widget.Toast

/**
 * Created by wanglikun on 2019/5/4
 */
class DatabaseHelper(private val mContext: Context, name: String?, factory: CursorFactory?, version: Int) : SQLiteOpenHelper(mContext, name, factory, version) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(CREATE_BOOK)
        db.execSQL(INSERT_BOOK)
        Toast.makeText(mContext, "创建成功", Toast.LENGTH_SHORT).show()
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {}

    companion object {
        const val CREATE_BOOK = "create table Book (" +
                "id integer primary key autoincrement, " +
                "author text, " +
                "price real, " +
                "page integer, " +
                "name text)"
        const val INSERT_BOOK = "insert into Book (" +
                "author," +
                "price," +
                "page," +
                "name" + ")" +
                "values (" +
                "'jint'," +
                "100," +
                "1000," +
                "'从入门到放弃')"
    }

}
