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
package com.didichuxing.doraemonkit.okgo.cache;

import android.content.ContentValues;
import android.database.Cursor;

import com.didichuxing.doraemonkit.okgo.model.HttpHeaders;
import com.didichuxing.doraemonkit.okgo.utils.IOUtils;

import java.io.Serializable;

/**
 * ================================================
 * 作    者：jeasonlzy（廖子尧）Github地址：https://github.com/jeasonlzy
 * 版    本：1.0
 * 创建日期：16/9/11
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class CacheEntity<T> implements Serializable {
    private static final long serialVersionUID = -4337711009801627866L;

    public static final long CACHE_NEVER_EXPIRE = -1;        //缓存永不过期

    //表中的字段
    public static final String KEY = "key";
    public static final String LOCAL_EXPIRE = "localExpire";
    public static final String HEAD = "head";
    public static final String DATA = "data";

    private String key;                    // 缓存key
    private long localExpire;              // 缓存过期时间
    private HttpHeaders responseHeaders;   // 缓存的响应头
    private T data;                        // 缓存的实体数据
    private boolean isExpire;   //缓存是否过期该变量不必保存到数据库，程序运行起来后会动态计算

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public HttpHeaders getResponseHeaders() {
        return responseHeaders;
    }

    public void setResponseHeaders(HttpHeaders responseHeaders) {
        this.responseHeaders = responseHeaders;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public long getLocalExpire() {
        return localExpire;
    }

    public void setLocalExpire(long localExpire) {
        this.localExpire = localExpire;
    }

    public boolean isExpire() {
        return isExpire;
    }

    public void setExpire(boolean expire) {
        isExpire = expire;
    }

    /**
     * @param cacheTime 允许的缓存时间
     * @param baseTime  基准时间,小于当前时间视为过期
     * @return 是否过期
     */
    public boolean checkExpire(CacheMode cacheMode, long cacheTime, long baseTime) {
        //304的默认缓存模式,设置缓存时间无效,需要依靠服务端的响应头控制
        if (cacheMode == CacheMode.DEFAULT) return getLocalExpire() < baseTime;
        if (cacheTime == CACHE_NEVER_EXPIRE) return false;
        return getLocalExpire() + cacheTime < baseTime;
    }

    public static <T> ContentValues getContentValues(CacheEntity<T> cacheEntity) {
        ContentValues values = new ContentValues();
        values.put(KEY, cacheEntity.getKey());
        values.put(LOCAL_EXPIRE, cacheEntity.getLocalExpire());
        values.put(HEAD, IOUtils.toByteArray(cacheEntity.getResponseHeaders()));
        values.put(DATA, IOUtils.toByteArray(cacheEntity.getData()));
        return values;
    }

    public static <T> CacheEntity<T> parseCursorToBean(Cursor cursor) {
        CacheEntity<T> cacheEntity = new CacheEntity<>();
        cacheEntity.setKey(cursor.getString(cursor.getColumnIndex(KEY)));
        cacheEntity.setLocalExpire(cursor.getLong(cursor.getColumnIndex(LOCAL_EXPIRE)));
        cacheEntity.setResponseHeaders((HttpHeaders) IOUtils.toObject(cursor.getBlob(cursor.getColumnIndex(HEAD))));
        //noinspection unchecked
        cacheEntity.setData((T) IOUtils.toObject(cursor.getBlob(cursor.getColumnIndex(DATA))));
        return cacheEntity;
    }

    @Override
    public String toString() {
        return "CacheEntity{key='" + key + '\'' + //
               ", responseHeaders=" + responseHeaders + //
               ", data=" + data + //
               ", localExpire=" + localExpire + //
               '}';
    }
}
