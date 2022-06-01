/*
 *
 *  *    Copyright (C) 2019 Amit Shekhar
 *  *    Copyright (C) 2011 Android Open Source Project
 *  *
 *  *    Licensed under the Apache License, Version 2.0 (the "License");
 *  *    you may not use this file except in compliance with the License.
 *  *    You may obtain a copy of the License at
 *  *
 *  *        http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  *    Unless required by applicable law or agreed to in writing, software
 *  *    distributed under the License is distributed on an "AS IS" BASIS,
 *  *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  *    See the License for the specific language governing permissions and
 *  *    limitations under the License.
 *
 */

package com.didichuxing.doraemondemo.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;


import com.tencent.wcdb.DatabaseUtils;
import com.tencent.wcdb.database.SQLiteDatabase;
import com.tencent.wcdb.database.SQLiteOpenHelper;

import java.util.ArrayList;

public class PersonDBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "Person.db";
    public static final String PERSON_TABLE_NAME = "person";
    public static final String PERSON_COLUMN_ID = "id";
    public static final String PERSON_COLUMN_FIRST_NAME = "first_name";
    public static final String PERSON_COLUMN_LAST_NAME = "last_name";
    public static final String PERSON_COLUMN_ADDRESS = "address";
    private static final String DB_PASSWORD = "a_password";

    public PersonDBHelper(Context context) {
        super(context, DATABASE_NAME, DB_PASSWORD.getBytes(), null, 3, null);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "create table person " +
                        "(id integer primary key, first_name text, last_name text, address text)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS person");
        onCreate(db);
    }

    public boolean insertPerson(String firstName, String lastName, String address) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("first_name", firstName);
        contentValues.put("last_name", lastName);
        contentValues.put("address", address);
        db.insert("person", null, contentValues);
        db.close();
        return true;
    }

    public Cursor getData(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from person where id=" + id + "", null);
        return res;
    }

    public int numberOfRows() {
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, PERSON_TABLE_NAME);
        return numRows;
    }

    public boolean updatePerson(Integer id, String firstName, String lastName, String address, float mileage) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("first_name", firstName);
        contentValues.put("last_name", lastName);
        contentValues.put("address", address);
        db.update("person", contentValues, "id = ? ", new String[]{Integer.toString(id)});
        db.close();
        return true;
    }

    public Integer deletePerson(Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("person",
                "id = ? ",
                new String[]{Integer.toString(id)});
    }

    public ArrayList<String> getAllPerson() {
        ArrayList<String> arrayList = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from person", null);
        res.moveToFirst();

        while (!res.isAfterLast()) {
            arrayList.add(
                    res.getString(res.getColumnIndex(PERSON_COLUMN_FIRST_NAME)) + " " +
                            res.getString(res.getColumnIndex(PERSON_COLUMN_LAST_NAME)));
            res.moveToNext();
        }
        res.close();
        db.close();
        return arrayList;
    }

    public int count() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from person", null);
        try {
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                return cursor.getInt(0);
            } else {
                return 0;
            }
        } finally {
            cursor.close();
            db.close();
        }
    }
}
