package com.didichuxing.doraemonkit.kit.crash;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.widget.Toast;

import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.constant.CachesKey;
import com.didichuxing.doraemonkit.util.CacheUtils;
import com.didichuxing.doraemonkit.util.FileUtil;
import com.didichuxing.doraemonkit.util.LogHelper;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by wanglikun on 2019-06-12
 */
public class CrashCaptureManager implements Thread.UncaughtExceptionHandler {
    private static final String TAG = "CrashCaptureManager";

    private final Thread.UncaughtExceptionHandler mDefaultHandler;
    private final Handler mHandler;
    private Context mContext;

    private CrashCaptureManager() {
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        HandlerThread handlerThread = new HandlerThread(TAG);
        handlerThread.start();
        mHandler = new Handler(handlerThread.getLooper());
    }

    private static class Holder {
        private static final CrashCaptureManager INSTANCE = new CrashCaptureManager();
    }

    public static CrashCaptureManager getInstance() {
        return Holder.INSTANCE;
    }

    public void init(Context context) {
        mContext = context.getApplicationContext();
    }

    public void start() {
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    public void stop() {
        Thread.setDefaultUncaughtExceptionHandler(mDefaultHandler);
    }

    @Override
    public void uncaughtException(final Thread t, final Throwable e) {
        LogHelper.d(TAG, t.toString());
        LogHelper.d(TAG, Log.getStackTraceString(e));
        CacheUtils.saveObject(e, getCrashCacheFile());
        post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(mContext, mContext.getString(R.string.dk_crash_capture_tips), Toast.LENGTH_SHORT).show();
            }
        });
        postDelay(new Runnable() {
            @Override
            public void run() {
                if (mDefaultHandler != null) {
                    mDefaultHandler.uncaughtException(t, e);
                }
            }
        }, 2000);
    }

    private void post(Runnable r) {
        mHandler.post(r);
    }

    private void postDelay(Runnable r, long delayMillis) {
        mHandler.postDelayed(r, delayMillis);
    }

    public File getCrashCacheDir() {
        File dir = new File(mContext.getCacheDir() + File.separator + CachesKey.CRASH_HISTORY);
        if (!dir.exists()) {
            dir.mkdir();
        }
        return dir;
    }

    private File getCrashCacheFile() {
        String fileName = new Date().toString();
        return new File(getCrashCacheDir() + File.separator + fileName);
    }

    public void clearCacheHistory() {
        FileUtil.deleteDirectory(getCrashCacheDir());
    }

    public List<CrashInfo> getCrashCaches() {
        File[] caches = getCrashCacheDir().listFiles();
        List<CrashInfo> result = new ArrayList<>();
        if (caches == null) {
            return result;
        }
        for (File cache : caches) {
            Serializable serializable = CacheUtils.readObject(cache);
            if (serializable instanceof Throwable) {
                CrashInfo info = new CrashInfo((Throwable) serializable, cache.lastModified());
                result.add(info);
            }
        }
        return result;
    }
}