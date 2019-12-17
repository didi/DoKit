/*
 * Copyright 2016 jeasonlzy(廖子尧)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.didichuxing.doraemonkit.okgo.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.didichuxing.doraemonkit.okgo.OkGo;
import com.didichuxing.doraemonkit.okgo.cache.CacheEntity;
import com.didichuxing.doraemonkit.okgo.cookie.SerializableCookie;
import com.didichuxing.doraemonkit.okgo.model.Progress;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * ================================================
 * 作    者：jeasonlzy（廖子尧）Github地址：https://github.com/jeasonlzy
 * 版    本：1.0
 * 创建日期：16/9/11
 * 描    述：
 * 修订历史：
 * ================================================
 */
class DBHelper extends SQLiteOpenHelper {

    private static final String DB_CACHE_NAME = "okgo.db";
    private static final int DB_CACHE_VERSION = 1;
    static final String TABLE_CACHE = "cache";
    static final String TABLE_COOKIE = "cookie";
    static final String TABLE_DOWNLOAD = "download";
    static final String TABLE_UPLOAD = "upload";

    static final Lock lock = new ReentrantLock();

    private TableEntity cacheTableEntity = new TableEntity(TABLE_CACHE);
    private TableEntity cookieTableEntity = new TableEntity(TABLE_COOKIE);
    private TableEntity downloadTableEntity = new TableEntity(TABLE_DOWNLOAD);
    private TableEntity uploadTableEntity = new TableEntity(TABLE_UPLOAD);

    DBHelper() {
        this(OkGo.getInstance().getContext());
    }

    DBHelper(Context context) {
        super(context, DB_CACHE_NAME, null, DB_CACHE_VERSION);

        cacheTableEntity.addColumn(new ColumnEntity(CacheEntity.KEY, "VARCHAR", true, true))//
                .addColumn(new ColumnEntity(CacheEntity.LOCAL_EXPIRE, "INTEGER"))//
                .addColumn(new ColumnEntity(CacheEntity.HEAD, "BLOB"))//
                .addColumn(new ColumnEntity(CacheEntity.DATA, "BLOB"));

        cookieTableEntity.addColumn(new ColumnEntity(SerializableCookie.HOST, "VARCHAR"))//
                .addColumn(new ColumnEntity(SerializableCookie.NAME, "VARCHAR"))//
                .addColumn(new ColumnEntity(SerializableCookie.DOMAIN, "VARCHAR"))//
                .addColumn(new ColumnEntity(SerializableCookie.COOKIE, "BLOB"))//
                .addColumn(new ColumnEntity(SerializableCookie.HOST, SerializableCookie.NAME, SerializableCookie.DOMAIN));

        downloadTableEntity.addColumn(new ColumnEntity(Progress.TAG, "VARCHAR", true, true))//
                .addColumn(new ColumnEntity(Progress.URL, "VARCHAR"))//
                .addColumn(new ColumnEntity(Progress.FOLDER, "VARCHAR"))//
                .addColumn(new ColumnEntity(Progress.FILE_PATH, "VARCHAR"))//
                .addColumn(new ColumnEntity(Progress.FILE_NAME, "VARCHAR"))//
                .addColumn(new ColumnEntity(Progress.FRACTION, "VARCHAR"))//
                .addColumn(new ColumnEntity(Progress.TOTAL_SIZE, "INTEGER"))//
                .addColumn(new ColumnEntity(Progress.CURRENT_SIZE, "INTEGER"))//
                .addColumn(new ColumnEntity(Progress.STATUS, "INTEGER"))//
                .addColumn(new ColumnEntity(Progress.PRIORITY, "INTEGER"))//
                .addColumn(new ColumnEntity(Progress.DATE, "INTEGER"))//
                .addColumn(new ColumnEntity(Progress.REQUEST, "BLOB"))//
                .addColumn(new ColumnEntity(Progress.EXTRA1, "BLOB"))//
                .addColumn(new ColumnEntity(Progress.EXTRA2, "BLOB"))//
                .addColumn(new ColumnEntity(Progress.EXTRA3, "BLOB"));

        uploadTableEntity.addColumn(new ColumnEntity(Progress.TAG, "VARCHAR", true, true))//
                .addColumn(new ColumnEntity(Progress.URL, "VARCHAR"))//
                .addColumn(new ColumnEntity(Progress.FOLDER, "VARCHAR"))//
                .addColumn(new ColumnEntity(Progress.FILE_PATH, "VARCHAR"))//
                .addColumn(new ColumnEntity(Progress.FILE_NAME, "VARCHAR"))//
                .addColumn(new ColumnEntity(Progress.FRACTION, "VARCHAR"))//
                .addColumn(new ColumnEntity(Progress.TOTAL_SIZE, "INTEGER"))//
                .addColumn(new ColumnEntity(Progress.CURRENT_SIZE, "INTEGER"))//
                .addColumn(new ColumnEntity(Progress.STATUS, "INTEGER"))//
                .addColumn(new ColumnEntity(Progress.PRIORITY, "INTEGER"))//
                .addColumn(new ColumnEntity(Progress.DATE, "INTEGER"))//
                .addColumn(new ColumnEntity(Progress.REQUEST, "BLOB"))//
                .addColumn(new ColumnEntity(Progress.EXTRA1, "BLOB"))//
                .addColumn(new ColumnEntity(Progress.EXTRA2, "BLOB"))//
                .addColumn(new ColumnEntity(Progress.EXTRA3, "BLOB"));
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(cacheTableEntity.buildTableString());
        db.execSQL(cookieTableEntity.buildTableString());
        db.execSQL(downloadTableEntity.buildTableString());
        db.execSQL(uploadTableEntity.buildTableString());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (DBUtils.isNeedUpgradeTable(db, cacheTableEntity)) db.execSQL("DROP TABLE IF EXISTS " + TABLE_CACHE);
        if (DBUtils.isNeedUpgradeTable(db, cookieTableEntity)) db.execSQL("DROP TABLE IF EXISTS " + TABLE_COOKIE);
        if (DBUtils.isNeedUpgradeTable(db, downloadTableEntity)) db.execSQL("DROP TABLE IF EXISTS " + TABLE_DOWNLOAD);
        if (DBUtils.isNeedUpgradeTable(db, uploadTableEntity)) db.execSQL("DROP TABLE IF EXISTS " + TABLE_UPLOAD);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
