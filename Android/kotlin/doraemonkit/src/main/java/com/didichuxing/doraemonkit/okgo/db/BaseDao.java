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

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Pair;

import com.didichuxing.doraemonkit.okgo.utils.OkLogger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;

/**
 * ================================================
 * 作    者：jeasonlzy（廖子尧）Github地址：https://github.com/jeasonlzy
 * 版    本：1.0
 * 创建日期：16/9/11
 * 描    述：
 * 修订历史：
 * ================================================
 */
public abstract class BaseDao<T> {

    protected static String TAG;
    protected Lock lock;
    protected SQLiteOpenHelper helper;
    protected SQLiteDatabase database;

    public BaseDao(SQLiteOpenHelper helper) {
        TAG = getClass().getSimpleName();
        lock = DBHelper.lock;
        this.helper = helper;
        this.database = openWriter();
    }

    public SQLiteDatabase openReader() {
        return helper.getReadableDatabase();
    }

    public SQLiteDatabase openWriter() {
        return helper.getWritableDatabase();
    }

    protected final void closeDatabase(SQLiteDatabase database, Cursor cursor) {
        if (cursor != null && !cursor.isClosed()) cursor.close();
        if (database != null && database.isOpen()) database.close();
    }

    /** 插入一条记录 */
    public boolean insert(T t) {
        if (t == null) return false;
        long start = System.currentTimeMillis();
        lock.lock();
        try {
            database.beginTransaction();
            database.insert(getTableName(), null, getContentValues(t));
            database.setTransactionSuccessful();
            return true;
        } catch (Exception e) {
            OkLogger.printStackTrace(e);
        } finally {
            database.endTransaction();
            lock.unlock();
            OkLogger.v(TAG, System.currentTimeMillis() - start + " insertT");
        }
        return false;
    }

    /** 插入一条记录 */
    public long insert(SQLiteDatabase database, T t) {
        return database.insert(getTableName(), null, getContentValues(t));
    }

    /** 插入多条记录 */
    public boolean insert(List<T> ts) {
        if (ts == null) return false;
        long start = System.currentTimeMillis();
        lock.lock();
        try {
            database.beginTransaction();
            for (T t : ts) {
                database.insert(getTableName(), null, getContentValues(t));
            }
            database.setTransactionSuccessful();
            return true;
        } catch (Exception e) {
            OkLogger.printStackTrace(e);
        } finally {
            database.endTransaction();
            lock.unlock();
            OkLogger.v(TAG, System.currentTimeMillis() - start + " insertList");
        }
        return false;
    }

    public boolean insert(SQLiteDatabase database, List<T> ts) {
        try {
            for (T t : ts) {
                database.insert(getTableName(), null, getContentValues(t));
            }
            return true;
        } catch (Exception e) {
            OkLogger.printStackTrace(e);
            return false;
        }
    }

    /** 删除所有数据 */
    public boolean deleteAll() {
        return delete(null, null);
    }

    /** 删除所有数据 */
    public long deleteAll(SQLiteDatabase database) {
        return delete(database, null, null);
    }

    /** 根据条件删除数据库中的数据 */
    public boolean delete(String whereClause, String[] whereArgs) {
        long start = System.currentTimeMillis();
        lock.lock();
        try {
            database.beginTransaction();
            database.delete(getTableName(), whereClause, whereArgs);
            database.setTransactionSuccessful();
            return true;
        } catch (Exception e) {
            OkLogger.printStackTrace(e);
        } finally {
            database.endTransaction();
            lock.unlock();
            OkLogger.v(TAG, System.currentTimeMillis() - start + " delete");
        }
        return false;
    }

    /** 根据条件删除数据库中的数据 */
    public long delete(SQLiteDatabase database, String whereClause, String[] whereArgs) {
        return database.delete(getTableName(), whereClause, whereArgs);
    }

    public boolean deleteList(List<Pair<String, String[]>> where) {
        long start = System.currentTimeMillis();
        lock.lock();
        try {
            database.beginTransaction();
            for (Pair<String, String[]> pair : where) {
                database.delete(getTableName(), pair.first, pair.second);
            }
            database.setTransactionSuccessful();
            return true;
        } catch (Exception e) {
            OkLogger.printStackTrace(e);
        } finally {
            database.endTransaction();
            lock.unlock();
            OkLogger.v(TAG, System.currentTimeMillis() - start + " deleteList");
        }
        return false;
    }

    /**
     * replace 语句有如下行为特点
     * 1. replace语句会删除原有的一条记录， 并且插入一条新的记录来替换原记录。
     * 2. 一般用replace语句替换一条记录的所有列， 如果在replace语句中没有指定某列， 在replace之后这列的值被置空 。
     * 3. replace语句根据主键的值确定被替换的是哪一条记录
     * 4. 如果执行replace语句时， 不存在要替换的记录， 那么就会插入一条新的记录。
     * 5. replace语句不能根据where子句来定位要被替换的记录
     * 6. 如果新插入的或替换的记录中， 有字段和表中的其他记录冲突， 那么会删除那条其他记录。
     */
    public boolean replace(T t) {
        if (t == null) return false;
        long start = System.currentTimeMillis();
        lock.lock();
        try {
            database.beginTransaction();
            database.replace(getTableName(), null, getContentValues(t));
            database.setTransactionSuccessful();
            return true;
        } catch (Exception e) {
            OkLogger.printStackTrace(e);
        } finally {
            database.endTransaction();
            lock.unlock();
            OkLogger.v(TAG, System.currentTimeMillis() - start + " replaceT");
        }
        return false;
    }

    public long replace(SQLiteDatabase database, T t) {
        return database.replace(getTableName(), null, getContentValues(t));
    }

    public boolean replace(ContentValues contentValues) {
        long start = System.currentTimeMillis();
        lock.lock();
        try {
            database.beginTransaction();
            database.replace(getTableName(), null, contentValues);
            database.setTransactionSuccessful();
            return true;
        } catch (Exception e) {
            OkLogger.printStackTrace(e);
        } finally {
            database.endTransaction();
            lock.unlock();
            OkLogger.v(TAG, System.currentTimeMillis() - start + " replaceContentValues");
        }
        return false;
    }

    public long replace(SQLiteDatabase database, ContentValues contentValues) {
        return database.replace(getTableName(), null, contentValues);
    }

    public boolean replace(List<T> ts) {
        if (ts == null) return false;
        long start = System.currentTimeMillis();
        lock.lock();
        try {
            database.beginTransaction();
            for (T t : ts) {
                database.replace(getTableName(), null, getContentValues(t));
            }
            database.setTransactionSuccessful();
            return true;
        } catch (Exception e) {
            OkLogger.printStackTrace(e);
        } finally {
            database.endTransaction();
            lock.unlock();
            OkLogger.v(TAG, System.currentTimeMillis() - start + " replaceList");
        }
        return false;
    }

    public boolean replace(SQLiteDatabase database, List<T> ts) {
        try {
            for (T t : ts) {
                database.replace(getTableName(), null, getContentValues(t));
            }
            return true;
        } catch (Exception e) {
            OkLogger.printStackTrace(e);
            return false;
        }
    }

    /** 更新一条记录 */
    public boolean update(T t, String whereClause, String[] whereArgs) {
        if (t == null) return false;
        long start = System.currentTimeMillis();
        lock.lock();
        try {
            database.beginTransaction();
            database.update(getTableName(), getContentValues(t), whereClause, whereArgs);
            database.setTransactionSuccessful();
            return true;
        } catch (Exception e) {
            OkLogger.printStackTrace(e);
        } finally {
            database.endTransaction();
            lock.unlock();
            OkLogger.v(TAG, System.currentTimeMillis() - start + " updateT");
        }
        return false;
    }

    /** 更新一条记录 */
    public long update(SQLiteDatabase database, T t, String whereClause, String[] whereArgs) {
        return database.update(getTableName(), getContentValues(t), whereClause, whereArgs);
    }

    /** 更新一条记录 */
    public boolean update(ContentValues contentValues, String whereClause, String[] whereArgs) {
        long start = System.currentTimeMillis();
        lock.lock();
        try {
            database.beginTransaction();
            database.update(getTableName(), contentValues, whereClause, whereArgs);
            database.setTransactionSuccessful();
            return true;
        } catch (Exception e) {
            OkLogger.printStackTrace(e);
        } finally {
            database.endTransaction();
            lock.unlock();
            OkLogger.v(TAG, System.currentTimeMillis() - start + " updateContentValues");
        }
        return false;
    }

    /** 更新一条记录 */
    public long update(SQLiteDatabase database, ContentValues contentValues, String whereClause, String[] whereArgs) {
        return database.update(getTableName(), contentValues, whereClause, whereArgs);
    }

    /** 查询并返回所有对象的集合 */
    public List<T> queryAll(SQLiteDatabase database) {
        return query(database, null, null);
    }

    /** 按条件查询对象并返回集合 */
    public List<T> query(SQLiteDatabase database, String selection, String[] selectionArgs) {
        return query(database, null, selection, selectionArgs, null, null, null, null);
    }

    /** 查询满足条件的一个结果 */
    public T queryOne(SQLiteDatabase database, String selection, String[] selectionArgs) {
        List<T> query = query(database, null, selection, selectionArgs, null, null, null, "1");
        if (query.size() > 0) return query.get(0);
        return null;
    }

    /** 按条件查询对象并返回集合 */
    public List<T> query(SQLiteDatabase database, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy, String limit) {
        List<T> list = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = database.query(getTableName(), columns, selection, selectionArgs, groupBy, having, orderBy, limit);
            while (!cursor.isClosed() && cursor.moveToNext()) {
                list.add(parseCursorToBean(cursor));
            }
        } catch (Exception e) {
            OkLogger.printStackTrace(e);
        } finally {
            closeDatabase(null, cursor);
        }
        return list;
    }

    /** 查询并返回所有对象的集合 */
    public List<T> queryAll() {
        return query(null, null);
    }

    /** 按条件查询对象并返回集合 */
    public List<T> query(String selection, String[] selectionArgs) {
        return query(null, selection, selectionArgs, null, null, null, null);
    }

    /** 查询满足条件的一个结果 */
    public T queryOne(String selection, String[] selectionArgs) {
        long start = System.currentTimeMillis();
        List<T> query = query(null, selection, selectionArgs, null, null, null, "1");
        OkLogger.v(TAG, System.currentTimeMillis() - start + " queryOne");
        return query.size() > 0 ? query.get(0) : null;
    }

    /** 按条件查询对象并返回集合 */
    public List<T> query(String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy, String limit) {
        long start = System.currentTimeMillis();
        lock.lock();
        List<T> list = new ArrayList<>();
        Cursor cursor = null;
        try {
            database.beginTransaction();
            cursor = database.query(getTableName(), columns, selection, selectionArgs, groupBy, having, orderBy, limit);
            while (!cursor.isClosed() && cursor.moveToNext()) {
                list.add(parseCursorToBean(cursor));
            }
            database.setTransactionSuccessful();
        } catch (Exception e) {
            OkLogger.printStackTrace(e);
        } finally {
            closeDatabase(null, cursor);
            database.endTransaction();
            lock.unlock();
            OkLogger.v(TAG, System.currentTimeMillis() - start + " query");
        }
        return list;
    }

    public interface Action {
        void call(SQLiteDatabase database);
    }

    /** 用于给外界提供事物开启的模板 */
    public void startTransaction(Action action) {
        lock.lock();
        try {
            database.beginTransaction();
            action.call(database);
            database.setTransactionSuccessful();
        } catch (Exception e) {
            OkLogger.printStackTrace(e);
        } finally {
            database.endTransaction();
            lock.unlock();
        }
    }

    /** 获取对应的表名 */
    public abstract String getTableName();

    public abstract void unInit();

    /** 将Cursor解析成对应的JavaBean */
    public abstract T parseCursorToBean(Cursor cursor);

    /** 需要替换的列 */
    public abstract ContentValues getContentValues(T t);
}
