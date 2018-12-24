package com.didichuxing.doraemonkit.kit.crash;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.widget.Toast;

import com.didichuxing.doraemonkit.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class CrashHandlerManager implements Thread.UncaughtExceptionHandler {

    private static final String TAG = CrashHandlerManager.class.getSimpleName();
    private static CrashHandlerManager INSTANCE = new CrashHandlerManager();

    private String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/doraemon/Crash/";

    private Thread.UncaughtExceptionHandler mDefaultHandler;

    private Map<String, String> infos = new HashMap<String, String>();

    private DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private final int CRASH = 10;

    private Handler handler;

    private Context mContext;

    private Boolean isOpen = false;

    public static CrashHandlerManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new CrashHandlerManager();
        }
        return INSTANCE;
    }

    private CrashHandlerManager() {

    }

    public void init(Context context) {
        if (!isOpen) {
            isOpen = true;
            mContext = context.getApplicationContext();
            mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
            Thread.setDefaultUncaughtExceptionHandler(this);
            CrashHandlerThread crashHandlerThread = new CrashHandlerThread(TAG);
            crashHandlerThread.start();
            handler = new Handler(crashHandlerThread.getLooper(), crashHandlerThread);
        }
    }

    public void remove() {
        if (isOpen) {
            isOpen = false;
            Thread.setDefaultUncaughtExceptionHandler(mDefaultHandler);
            mDefaultHandler = null;
        }

    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        if (!handleException(ex) && mDefaultHandler != null) {
            mDefaultHandler.uncaughtException(thread, ex);
        } else {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(mContext, R.string.dk_crash_capture_tips, Toast.LENGTH_LONG).show();
                }
            });
        }


        if (!handleException(ex) && mDefaultHandler != null) {
            mDefaultHandler.uncaughtException(thread, ex);
        } else {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {

            }
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(0);
        }
    }


    private boolean handleException(Throwable ex) {
        if (ex == null) {
            return false;
        }
        handler.sendEmptyMessage(CRASH);

        collectDeviceInfo();

        saveCrashInfo2File(ex);
        return true;
    }

    private void collectDeviceInfo() {
        try {
            PackageManager pm = mContext.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(mContext.getPackageName(),
                    PackageManager.GET_ACTIVITIES);
            if (pi != null) {
                String versionName = pi.versionName == null ? "null"
                        : pi.versionName;
                String versionCode = pi.versionCode + "";
                infos.put("versionName", versionName);
                infos.put("versionCode", versionCode);
            }
        } catch (PackageManager.NameNotFoundException e) {

        }
        Field[] fields = Build.class.getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                infos.put(field.getName(), field.get(null).toString());
            } catch (Exception e) {
            }
        }
    }

    public String getFilePath() {
        return path;
    }

    private String saveCrashInfo2File(Throwable ex) {

        StringBuffer sb = new StringBuffer();
        for (Map.Entry<String, String> entry : infos.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            sb.append(key + "=" + value + "\n");
        }

        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        ex.printStackTrace(printWriter);
        Throwable cause = ex.getCause();
        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }
        printWriter.close();
        String result = writer.toString();
        sb.append(result);
        try {
            String fileName = formatter.format(new Date()) + ".txt";
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                File dir = new File(path);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                FileOutputStream fos = new FileOutputStream(path + fileName);
                fos.write(sb.toString().getBytes());
                fos.close();
            }
            return fileName;
        } catch (Exception e) {

        }
        return null;
    }

    public void cleanHistoricalData() {
        try {
            File file = new File(getFilePath());
            if (file.exists()) {
                RecursionDeleteFile(file);
            }
        } catch (Exception e) {

        }

    }

    public void RecursionDeleteFile(File file) {
        if (file.isFile()) {
            file.delete();
            return;
        }
        if (file.isDirectory()) {
            File[] childFile = file.listFiles();
            if (childFile == null || childFile.length == 0) {
                file.delete();
                return;
            }
            for (File f : childFile) {
                RecursionDeleteFile(f);
            }
            file.delete();
        }
    }

    private class CrashHandlerThread extends HandlerThread implements android.os.Handler.Callback {

        public CrashHandlerThread(String name) {
            super(name);
        }


        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == CRASH) {
                Toast.makeText(mContext, R.string.dk_crash_capture_tips, Toast.LENGTH_LONG).show();
            }
            return true;
        }
    }

}