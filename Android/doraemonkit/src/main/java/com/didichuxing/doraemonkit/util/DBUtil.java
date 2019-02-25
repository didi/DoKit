package com.didichuxing.doraemonkit.util;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class DBUtil {

    public static List<String> queryDBtableName(SQLiteDatabase database) {
        ArrayList<String> tableName = new ArrayList<>();

        if (database == null) {
            return tableName;
        }
        Cursor cursor = database.rawQuery("select name from sqlite_master where type='table' order by name", null);
        while (cursor.moveToNext()) {
            tableName.add(cursor.getString(0));
        }

        return tableName;
    }

    public static String[] queryTableColumnName(SQLiteDatabase database, String tableName) {
        ArrayList<String> columnNames = new ArrayList<>();

        if (tableName == null || database == null) {
            return new String[0];
        }
        Cursor cursor = database.rawQuery("PRAGMA table_info([" + tableName + "])", null);
        // 对应 cid，name，type，notnull，dfl_value,pk
        while (cursor.moveToNext()) {
            columnNames.add(cursor.getString(1));
        }
        return columnNames.toArray(new String[columnNames.size()]);
    }

    public static String[][] queryAll(SQLiteDatabase database, String table) {
        String[] strings = queryTableColumnName(database, table);
        Cursor cursor = database.rawQuery("select * from " + table, null);
        int rowCount = cursor.getCount();
        String[][] words = new String[strings.length][rowCount];
        for (int y = 0; y <rowCount; y++) {
            if (cursor.moveToNext()) {
                for (int x = 0; x < strings.length; x++) {
                    words[x][y] = cursor.getString(x);
                }
            }
        }
        return words;
    }

}
