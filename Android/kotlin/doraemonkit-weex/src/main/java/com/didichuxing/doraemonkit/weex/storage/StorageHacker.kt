package com.didichuxing.doraemonkit.weex.storage

import android.app.Application
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import org.apache.weex.WXSDKEngine
import org.apache.weex.appfram.storage.DefaultWXStorage
import org.apache.weex.appfram.storage.IWXStorageAdapter
import org.apache.weex.appfram.storage.WXSQLiteOpenHelper

/**
 * Transformed by alvince on 2020/7/1
 *
 * @author haojianglong
 * @date 2019-06-18
 */
class StorageHacker(context: Context, private val isDebug: Boolean) {

    companion object {
        const val TAG = "StorageHacker"
    }

    private var storageAdapter: IWXStorageAdapter? = WXSDKEngine.getIWXStorageAdapter()

    private var hackerContext: Context = context
        .let {
            if (it is Application) it else it.applicationContext
        }

    private var disposed = false

    fun destroy() {
        if (disposed) {
            return
        }
        disposed = true
    }

    fun isDestroy(): Boolean = disposed

    suspend fun fetch(): List<StorageInfo> {
        if (storageAdapter == null
            || storageAdapter !is DefaultWXStorage
        ) {
            return emptyList()
        }

        if (isDestroy()) {
            return emptyList()
        }

        val result = mutableListOf<StorageInfo>()
        var sqliteHelper: WXSQLiteOpenHelper? = null
        try {
            sqliteHelper = WXSQLiteOpenHelper::class.java.getDeclaredConstructor(Context::class.java)
                .let { constructor ->
                    constructor.isAccessible = true
                    constructor.newInstance(hackerContext)
                }
            WXSQLiteOpenHelper::class.java.getDeclaredMethod("getDatabase")
                .let { method ->
                    method.isAccessible = true
                    method.invoke(sqliteHelper) as? SQLiteDatabase
                }
                ?.also { database ->
                    database.query(
                        "default_wx_storage", arrayOf("key", "value", "timestamp"),
                        null, null, null, null, null
                    ).use { cursor ->
                        if (isDebug) {
                            Log.d("weex-analyzer", "start dump weex storage")
                        }
                        while (cursor.moveToNext()) {
                            StorageInfo().apply {
                                key = cursor.stringAt("key")
                                value = cursor.stringAt("value")
                                timestamp = cursor.stringAt("timestamp")
                            }.also { info ->
                                if (isDebug) {
                                    Log.d("weex-analyzer", "weex storage[${info.key} | ${info.value}]")
                                }
                                result.add(info)
                            }
                        }
                        if (isDebug) {
                            Log.d("weex-analyzer", "end dump weex storage")
                        }
                    }
                }
        } catch (ex: Exception) {
            Log.e(TAG, "", ex)
        } finally {
            sqliteHelper?.closeDatabase()
        }
        return result
    }

    suspend fun remove(key: String): Boolean {
        if (key.isEmpty()) {
            return false
        }

        if (storageAdapter == null
            || storageAdapter !is DefaultWXStorage
        ) {
            return false
        }

        if (isDestroy()) {
            return false
        }

        return (storageAdapter as? DefaultWXStorage)?.let { storage ->
            try {
                storage.javaClass.getDeclaredMethod("performRemoveItem", String::class.java)
                    .let { method ->
                        method.isAccessible = true
                        val result = method.invoke(storage, key)?.let { it as? Boolean }
                        method.isAccessible = false
                        result
                    }
            } catch (ex: Exception) {
                Log.d(TAG, "Fail to resolve storage method: performRemoveItem(String)", ex)
                false
            }
        } == true
    }

    private fun Cursor.stringAt(column: String): String {
        if (column.isEmpty()) {
            return ""
        }
        return getColumnIndex(column)
            .takeIf { it != -1 }
            ?.let { index -> getString(index) }
            ?: ""
    }

}
