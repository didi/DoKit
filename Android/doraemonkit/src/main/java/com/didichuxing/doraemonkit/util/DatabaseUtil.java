package com.didichuxing.doraemonkit.util;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DatabaseUtil {
    private DatabaseUtil() {
    }

    public static List<String> queryTableName(SQLiteDatabase database) {
        ArrayList<String> tableName = new ArrayList<>();

        if (database == null) {
            return tableName;
        }
        Cursor cursor = database.rawQuery("select name from sqlite_master where type='table' order by name", null);
        while (cursor.moveToNext()) {
            tableName.add(cursor.getString(0));
        }
        cursor.close();
        return tableName;
    }

    public static String[] queryTableColumnName(SQLiteDatabase database, String tableName) {
        if (tableName == null || database == null) {
            return new String[0];
        }
        Cursor cursor = database.query(tableName, null, null, null, null, null, null);
        ArrayList<String> columnNames = new ArrayList<>(Arrays.asList(cursor.getColumnNames()));
        cursor.close();
        return columnNames.toArray(new String[0]);
    }

    public static String[][] queryAll(SQLiteDatabase database, String tableName) {
        String[] strings = queryTableColumnName(database, tableName);
        Cursor cursor = database.query(tableName, null, null, null, null, null, null);
        int rowCount = cursor.getCount();
        String[][] words = new String[strings.length][rowCount];
        for (int y = 0; y < rowCount; y++) {
            if (cursor.moveToNext()) {
                for (int x = 0; x < strings.length; x++) {
                    if (cursor.getType(x) == Cursor.FIELD_TYPE_BLOB) {
                        words[x][y] = new String(cursor.getBlob(x));
                    } else {
                        words[x][y] = cursor.getString(x);
                    }
                }
            }
        }
        cursor.close();
        return words;
    }
}
