package com.didichuxing.doraemonkit.weex.storage

import android.app.Application
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.taobao.weex.WXSDKEngine
import com.taobao.weex.appfram.storage.DefaultWXStorage
import com.taobao.weex.appfram.storage.IWXStorageAdapter
import com.taobao.weex.appfram.storage.WXSQLiteOpenHelper
import java.util.concurrent.Executors

/**
 * Transformed by alvince on 2020/7/1
 *
 * @author haojianglong
 * @date 2019-06-18
 */
class StorageHacker(context: Context, private val isDebug: Boolean) {

    interface OnLoadListener {
        fun onLoad(list: List<StorageInfo>)
    }

    interface OnRemoveListener {
        fun onRemoved(status: Boolean)
    }

    companion object {
        const val TAG = "StorageHacker"
    }

    private val handler by lazy { Handler(Looper.getMainLooper()) }

    private val executor by lazy {
        Executors.newCachedThreadPool { runnable -> Thread(runnable, "wx_analyzer_storage_dumper") }
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
        handler.removeCallbacksAndMessages(null)
        executor.takeIf { !it.isShutdown }?.shutdown()
        disposed = true
    }

    fun isDestroy(): Boolean = disposed || executor.isShutdown

    fun fetch(listener: OnLoadListener) {
        if (storageAdapter == null
            || storageAdapter !is DefaultWXStorage
        ) {
            listener.onLoad(emptyList())
            return
        }

        if (isDestroy()) {
            listener.onLoad(emptyList())
            return
        }

        executor.execute {
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
                        val result = mutableListOf<StorageInfo>()
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
                        handler.post { listener.onLoad(result) }
                    }
            } catch (ex: Exception) {
                Log.e(TAG, "", ex)
            } finally {
                sqliteHelper?.closeDatabase()
            }
        }
    }

    fun remove(key: String, listener: OnRemoveListener) {
        if (key.isEmpty()) {
            return
        }

        if (storageAdapter == null
            || storageAdapter !is DefaultWXStorage
        ) {
            listener.onRemoved(false)
            return
        }

        if (isDestroy()) {
            listener.onRemoved(false)
            return
        }

        executor.execute {
            (storageAdapter as? DefaultWXStorage)?.also { storage ->
                try {
                    storage.javaClass.getDeclaredMethod("performRemoveItem", String::class.java)
                        .also { method ->
                            method.isAccessible = true
                            method.invoke(storage, key)
                                ?.let { it as? Boolean }
                                ?.also { result ->
                                    handler.post {
                                        listener.onRemoved(result)
                                    }
                                }
                            method.isAccessible = false
                        }
                } catch (ex: Exception) {
                    Log.d(TAG, "Fail to resolve storage method: performRemoveItem(String)", ex)
                }
            }
        }
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