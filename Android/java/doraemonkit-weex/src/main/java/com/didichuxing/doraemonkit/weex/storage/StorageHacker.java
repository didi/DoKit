package com.didichuxing.doraemonkit.weex.storage;

import android.app.Application;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Looper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import org.apache.weex.WXSDKEngine;
import org.apache.weex.appfram.storage.DefaultWXStorage;
import org.apache.weex.appfram.storage.IWXStorageAdapter;
import org.apache.weex.appfram.storage.WXSQLiteOpenHelper;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * @author haojianglong
 * @date 2019-06-18
 */

public final class StorageHacker {

    private IWXStorageAdapter mStorageAdapter;
    private Context mContext;
    private final boolean isDebug;

    private Handler mHandler = new Handler(Looper.getMainLooper());
    private ExecutorService mExecutor = Executors.newCachedThreadPool(new ThreadFactory() {
        @Override
        public Thread newThread(@NonNull Runnable r) {
            return new Thread(r, "wx_analyzer_storage_dumper");
        }
    });

    public StorageHacker(@NonNull Context context, boolean isDebug) {
        mStorageAdapter = WXSDKEngine.getIWXStorageAdapter();
        if (!(context instanceof Application)) {
            context = context.getApplicationContext();
        }
        this.mContext = context;
        this.isDebug = isDebug;
    }

    public void destroy() {
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }

        if (mExecutor != null) {
            mExecutor.shutdown();
            mExecutor = null;
        }
    }

    public boolean isDestroy() {
        return mHandler == null || mExecutor == null || mExecutor.isShutdown();
    }


    @SuppressWarnings("unchecked")
    public void fetch(@Nullable final OnLoadListener listener) {
        if (listener == null) {
            return;
        }

        if (mStorageAdapter == null) {
            listener.onLoad(Collections.<StorageInfo>emptyList());
            return;
        }

        if (!(mStorageAdapter instanceof DefaultWXStorage)) {
            listener.onLoad(Collections.<StorageInfo>emptyList());
            return;
        }

        if (isDestroy()) {
            listener.onLoad(Collections.<StorageInfo>emptyList());
            return;
        }

        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                final List<StorageInfo> resultList = new ArrayList<>();
                WXSQLiteOpenHelper helper = null;
                Cursor c = null;
                try {
                    Constructor<WXSQLiteOpenHelper> constructor = WXSQLiteOpenHelper.class.getDeclaredConstructor(Context.class);
                    constructor.setAccessible(true);
                    helper = constructor.newInstance(mContext);

                    Method method = WXSQLiteOpenHelper.class.getDeclaredMethod("getDatabase");
                    method.setAccessible(true);
                    SQLiteDatabase db = (SQLiteDatabase) method.invoke(helper);

                    c = db.query("default_wx_storage", new String[]{"key", "value", "timestamp"}, null, null, null, null, null);

                    if (isDebug) {
                        Log.d("weex-analyzer", "start dump weex storage");
                    }

                    while (c.moveToNext()) {
                        StorageInfo info = new StorageInfo();
                        info.key = c.getString(c.getColumnIndex("key"));
                        info.value = c.getString(c.getColumnIndex("value"));
                        info.timestamp = c.getString(c.getColumnIndex("timestamp"));
                        if (isDebug) {
                            Log.d("weex-analyzer", "weex storage[" + info.key + " | " + info.value + "]");
                        }
                        resultList.add(info);
                    }

                    if (isDebug) {
                        Log.d("weex-analyzer", "end dump weex storage");
                    }

                    if (mHandler != null) {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                listener.onLoad(resultList);
                            }
                        });
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (c != null) {
                        c.close();
                    }
                    if (helper != null) {
                        helper.closeDatabase();
                    }
                }
            }
        });
    }

    public void remove(@Nullable final String key, @Nullable final OnRemoveListener listener) {
        if (listener == null || TextUtils.isEmpty(key)) {
            return;
        }

        if (mStorageAdapter == null) {
            listener.onRemoved(false);
            return;
        }

        if (!(mStorageAdapter instanceof DefaultWXStorage)) {
            listener.onRemoved(false);
            return;
        }

        if (isDestroy()) {
            listener.onRemoved(false);
            return;
        }

        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    DefaultWXStorage storage = (DefaultWXStorage) mStorageAdapter;
                    Method method = storage.getClass().getDeclaredMethod("performRemoveItem", String.class);
                    if (method != null) {
                        method.setAccessible(true);
                        final boolean result = (boolean) method.invoke(storage, key);
                        if (mHandler != null) {
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    listener.onRemoved(result);
                                }
                            });
                            method.setAccessible(false);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    interface OnLoadListener {
        void onLoad(List<StorageInfo> list);
    }

    interface OnRemoveListener {
        void onRemoved(boolean status);
    }

}
