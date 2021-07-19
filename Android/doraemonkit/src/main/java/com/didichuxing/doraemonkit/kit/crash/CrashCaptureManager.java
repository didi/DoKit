package com.didichuxing.doraemonkit.kit.crash;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.View;

import androidx.core.view.ViewCompat;

import com.didichuxing.doraemonkit.util.ToastUtils;
import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.constant.CachesKey;
import com.didichuxing.doraemonkit.datapick.DataPickManager;
import com.didichuxing.doraemonkit.util.DoKitCacheUtils;
import com.didichuxing.doraemonkit.util.DoKitFileUtil;
import com.didichuxing.doraemonkit.util.DoKitImageUtil;

import java.io.File;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;

/**
 * Created by wanglikun on 2019-06-12
 * 系统崩溃异常捕获
 */
public class CrashCaptureManager implements Thread.UncaughtExceptionHandler {
    private static final String TAG = "CrashCaptureManager";

    private final Thread.UncaughtExceptionHandler mDefaultHandler;
    private final Handler mHandler;
    private Context mContext;
    private SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyyMMdd_HHmmdd");
    private String mFilenamePrefix;

    private CrashCaptureManager() {
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        HandlerThread handlerThread = new HandlerThread(TAG);
        handlerThread.start();
        mHandler = new Handler(handlerThread.getLooper());
        generateFilenamePrefix();
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
        //让log和图片统一文件名 每次生成新的文件名
        generateFilenamePrefix();
        //异步保存崩溃截图在华为emui10.0上会失效 已改成同步
        asyncSaveCrashScreenshot();
        //保存崩溃信息
        DoKitCacheUtils.saveObject((Serializable) Log.getStackTraceString(e), getCrashCacheFile());
        //保存埋点数据
        DataPickManager.getInstance().saveData2Local();
        post(new Runnable() {
            @Override
            public void run() {
                ToastUtils.showShort(mContext.getString(R.string.dk_crash_capture_tips));
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
        return new File(getCrashCacheDir(), String.format("%s.log", mFilenamePrefix));
    }

    private File getCrashCacheScreenshotFile(int num) {
        return new File(getCrashCacheDir(), String.format("%s_%d.png", mFilenamePrefix, num));
    }

    public void clearCacheHistory() {
        DoKitFileUtil.deleteDirectory(getCrashCacheDir());
    }

    private void generateFilenamePrefix() {
        mFilenamePrefix = mDateFormat.format(System.currentTimeMillis());
    }

    public void asyncSaveCrashScreenshot() {
        saveCrashScreenshot();
//        AsyncTask.THREAD_POOL_EXECUTOR.execute(new Runnable() {
//            @Override
//            public void run() {
//                saveCrashScreenshot();
//            }
//        });
    }

    public void saveCrashScreenshot() {
        try {
            Class<?> wmgClass = Class.forName("android.view.WindowManagerGlobal");
            Object wmgInstance = wmgClass.getMethod("getInstance").invoke(null);
            Method getViewRootNames = wmgClass.getMethod("getViewRootNames");
            Method getRootView = wmgClass.getMethod("getRootView", String.class);
            String[] rootViewNames = (String[]) getViewRootNames.invoke(wmgInstance);
            if (rootViewNames != null) {
                for (int i = 0; i < rootViewNames.length; i++) {
                    View rootView = (View) getRootView.invoke(wmgInstance, rootViewNames[i]);
                    if (rootView != null) {
                        if (!ViewCompat.isLaidOut(rootView)) {
                            Log.d(TAG, "View尚未绘制完成，可能无法成功截取图片");
                        }
                        Bitmap bitmap = Bitmap.createBitmap(rootView.getWidth(), rootView.getHeight(), Bitmap.Config.ARGB_8888);
                        Canvas canvas = new Canvas(bitmap);
                        rootView.draw(canvas);

                        File output = getCrashCacheScreenshotFile(i);
                        DoKitImageUtil.bitmap2File(bitmap, 100, output);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}