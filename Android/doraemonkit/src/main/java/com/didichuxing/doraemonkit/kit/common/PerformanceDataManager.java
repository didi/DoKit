package com.didichuxing.doraemonkit.kit.common;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;
import android.os.Debug;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.os.Process;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.view.Choreographer;
import android.widget.Toast;

import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.adb.AdbManager;
import com.didichuxing.doraemonkit.adb.Callback;
import com.didichuxing.doraemonkit.util.FileManager;
import com.didichuxing.doraemonkit.util.LogHelper;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by wanglikun on 2018/9/13.
 */

public class PerformanceDataManager {
    private static final String TAG = "PerformanceDataManager";
    private static final float SECOND_IN_NANOS = 1000000000f;
    private static final int NORMAL_FRAME_RATE = 1;
    private String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/doraemon/";
    private String memoryFileName = "memory.txt";
    private String cpuFileName = "cpu.txt";
    private String fpsFileName = "fps.txt";

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private long mLastFrameTimeNanos;
    private int mLastFrameRate;
    private int mLastSkippedFrames;
    private float mLastCpuRate;
    private float mLastMemoryInfo;
    private String mPackageName;
    private Handler mHandler;
    private HandlerThread mHandlerThread;
    private float mMaxMemory;
    private Context mContext;
    private ActivityManager mActivityManager;
    private RandomAccessFile mProcStatFile;
    private RandomAccessFile mAppStatFile;
    private Long mLastCpuTime;
    private Long mLastAppCpuTime;
    private boolean mAboveAndroidO; // 是否是8.0及其以上
    private boolean mHasRemindUser;
    private static final int MSG_CPU = 1;
    private static final int MSG_MEMORY = 2;
    private static final int MSG_REMIND = 3;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private Choreographer.FrameCallback mFrameCallback = new Choreographer.FrameCallback() {
        @Override
        public void doFrame(long frameTimeNanos) {
            if (mLastFrameTimeNanos != 0L) {
                long temp = frameTimeNanos - mLastFrameTimeNanos;
                if (temp != 0) {
                    mLastFrameRate = Math.round(SECOND_IN_NANOS / (frameTimeNanos - mLastFrameTimeNanos));
                    mLastSkippedFrames = 60 - mLastFrameRate;
                }
            }
            mLastFrameTimeNanos = frameTimeNanos;
            Choreographer.getInstance().postFrameCallback(this);
            writeFpsDataIntoFile();
        }
    };

    private void excuteCpuData() {
        LogHelper.d(TAG, "current thread name is ==" + Thread.currentThread().getName());
        if (mAboveAndroidO) {
            //8.0之后由于权限问题只能通过adb的方式获取
            AdbManager.getInstance().performAdbRequest("shell:dumpsys cpuinfo | grep '" + mPackageName + "'",
                    new Callback() {
                        @Override
                        public void onSuccess(String adbResponse) {
                            LogHelper.d(TAG, "response is " + adbResponse);
                            try {
                                mLastCpuRate = parseCPUData(adbResponse);
                                writeCpuDataIntoFile();
                            } catch (IOException e) {
                                LogHelper.d(TAG, "parse data fail " + e.getMessage());
                                mHandler.sendEmptyMessage(MSG_REMIND);
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFail(String failString) {
                            mHandler.sendEmptyMessage(MSG_REMIND);
                            LogHelper.d(TAG, "failString is " + failString);
                        }
                    });
        } else {
            mLastCpuRate = getCPUData();
            LogHelper.d(TAG, "cpu info is =" + mLastCpuRate);
            writeCpuDataIntoFile();
        }
    }

    private void excuteMemoruData() {
        if (mAboveAndroidO) {
            //8.0之后由于权限问题只能通过adb的方式获取
            AdbManager.getInstance().performAdbRequest("shell:dumpsys meminfo | grep '" + mPackageName + "'",
                    new Callback() {
                        @Override
                        public void onSuccess(String adbResponse) {
                            LogHelper.d(TAG, "response is " + adbResponse);
                            try {
                                mLastMemoryInfo = parseMemoryData(adbResponse);
                                writeMemoryDataIntoFile();
                            } catch (IOException e) {
                                mHandler.sendEmptyMessage(MSG_REMIND);
                                LogHelper.d(TAG, "parse data fail " + e.getMessage());
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFail(String failString) {
                            mHandler.sendEmptyMessage(MSG_REMIND);
                            LogHelper.d(TAG, "failString is " + failString);
                        }
                    });
        } else {
            mLastMemoryInfo = getMemoryData();
            LogHelper.d(TAG, "memory info is =" + mLastMemoryInfo);
            writeMemoryDataIntoFile();
        }
    }

    private void remindUserToConnectPort() {
        if (!mHasRemindUser) {
            mHasRemindUser = true;
            Toast.makeText(mContext, R.string.dk_cpu_memory_remind_user, Toast.LENGTH_LONG).show();
        }
    }

    private static class Holder {
        private static PerformanceDataManager INSTANCE = new PerformanceDataManager();
    }

    private PerformanceDataManager() {
    }

    public static PerformanceDataManager getInstance() {
        return Holder.INSTANCE;
    }

    public void init(Context context) {
        mContext = context;
        mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mAboveAndroidO = true;
            mPackageName = context.getPackageName();
            AdbManager.getInstance().init(context);
        }
        if (mHandlerThread == null) {
            mHandlerThread = new HandlerThread("handler-thread");
            mHandlerThread.start();
        }
        if (mHandler == null) {
            mHandler = new Handler(mHandlerThread.getLooper()) {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    if (msg.what == MSG_CPU) {
                        excuteCpuData();
                        mHandler.sendEmptyMessageDelayed(MSG_CPU, NORMAL_FRAME_RATE * 1000);
                    } else if (msg.what == MSG_MEMORY) {
                        excuteMemoruData();
                        mHandler.sendEmptyMessageDelayed(MSG_MEMORY, NORMAL_FRAME_RATE * 1000);
                    } else if (msg.what == MSG_REMIND) {
                        remindUserToConnectPort();
                    }
                }
            };
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void startMonitorFrameInfo() {
        Choreographer.getInstance().postFrameCallback(mFrameCallback);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void stopMonitorFrameInfo() {
        Choreographer.getInstance().removeFrameCallback(mFrameCallback);
    }

    public void startMonitorCPUInfo() {
        mHandler.sendEmptyMessageDelayed(MSG_CPU, NORMAL_FRAME_RATE * 1000);
    }

    public void stopMonitorCPUInfo() {
        mHandler.removeMessages(MSG_CPU);
    }

    public void destroy() {
        stopMonitorMemoryInfo();
        stopMonitorCPUInfo();
        stopMonitorFrameInfo();
        if (mHandlerThread != null) {
            mHandlerThread.quit();
        }
        mHandlerThread = null;
        mHandler = null;
    }

    public void startMonitorMemoryInfo() {
        if (mMaxMemory == 0) {
            mMaxMemory = mActivityManager.getMemoryClass();
        }
        mHandler.sendEmptyMessageDelayed(MSG_MEMORY, NORMAL_FRAME_RATE * 1000);
    }

    public void stopMonitorMemoryInfo() {
        mHandler.removeMessages(MSG_MEMORY);
    }

    private void writeCpuDataIntoFile() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(mLastCpuRate);
        stringBuilder.append(" ");
        stringBuilder.append(simpleDateFormat.format(new Date(System.currentTimeMillis())));
        FileManager.writeTxtToFile(stringBuilder.toString(), filePath, cpuFileName);
    }

    private void writeMemoryDataIntoFile() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(mLastMemoryInfo);
        stringBuilder.append(" ");
        stringBuilder.append(simpleDateFormat.format(new Date(System.currentTimeMillis())));
        FileManager.writeTxtToFile(stringBuilder.toString(), filePath, memoryFileName);
    }

    private void writeFpsDataIntoFile() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(mLastFrameRate);
        stringBuilder.append(" ");
        stringBuilder.append(simpleDateFormat.format(new Date(System.currentTimeMillis())));
        FileManager.writeTxtToFile(stringBuilder.toString(), filePath, fpsFileName);
    }

    private float getCPUData() {
        long cpuTime;
        long appTime;
        float value = 0.0f;
        try {
            if (mProcStatFile == null || mAppStatFile == null) {
                mProcStatFile = new RandomAccessFile("/proc/stat", "r");
                mAppStatFile = new RandomAccessFile("/proc/" + android.os.Process.myPid() + "/stat", "r");
            } else {
                mProcStatFile.seek(0L);
                mAppStatFile.seek(0L);
            }
            String procStatString = mProcStatFile.readLine();
            String appStatString = mAppStatFile.readLine();
            String procStats[] = procStatString.split(" ");
            String appStats[] = appStatString.split(" ");
            cpuTime = Long.parseLong(procStats[2]) + Long.parseLong(procStats[3])
                    + Long.parseLong(procStats[4]) + Long.parseLong(procStats[5])
                    + Long.parseLong(procStats[6]) + Long.parseLong(procStats[7])
                    + Long.parseLong(procStats[8]);
            appTime = Long.parseLong(appStats[13]) + Long.parseLong(appStats[14]);
            if (mLastCpuTime == null && mLastAppCpuTime == null) {
                mLastCpuTime = cpuTime;
                mLastAppCpuTime = appTime;
                return value;
            }
            value = ((float) (appTime - mLastAppCpuTime) / (float) (cpuTime - mLastCpuTime)) * 100f;
            mLastCpuTime = cpuTime;
            mLastAppCpuTime = appTime;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }

    private float getMemoryData() {
        float mem = 0.0F;
        try {
            // 统计进程的内存信息 totalPss
            final Debug.MemoryInfo[] memInfo = mActivityManager.getProcessMemoryInfo(new int[]{Process.myPid()});
            if (memInfo.length > 0) {
                // TotalPss = dalvikPss + nativePss + otherPss, in KB
                final int totalPss = memInfo[0].getTotalPss();
                if (totalPss >= 0) {
                    // Mem in MB
                    mem = totalPss / 1024.0F;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mem;
    }

    private float parseMemoryData(String data) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(data.getBytes())));
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            line = line.trim();
            if (line.contains("Permission Denial")) {
                break;
            } else {
                String[] lineItems = line.split("\\s+");
                if (lineItems != null && lineItems.length > 1) {
                    String result = lineItems[0];
                    LogHelper.d(TAG, "result is ==" + result);
                    bufferedReader.close();
                    if (!TextUtils.isEmpty(result) && result.contains("K:")) {
                        result = result.replace("K:", "");
                        if (result.contains(",")) {
                            result = result.replace(",", ".");
                        }
                    }
                    // Mem in MB
                    return Float.parseFloat(result) / 1024;
                }
            }
        }
        return 0;
    }

    private float parseCPUData(String data) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(data.getBytes())));
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            line = line.trim();
            if (line.contains("Permission Denial")) {
                break;
            } else {
                String[] lineItems = line.split("\\s+");
                if (lineItems != null && lineItems.length > 1) {
                    LogHelper.d(TAG, "result is ==" + lineItems[0]);
                    bufferedReader.close();
                    return Float.parseFloat(lineItems[0].replace("%", ""));
                }
            }
        }
        return 0;
    }

    public String getCpuFilePath() {
        return filePath + cpuFileName;
    }

    public String getMemoryFilePath() {
        return filePath + memoryFileName;
    }

    public String getFpsFilePath() {
        return filePath + fpsFileName;
    }

    public long getLastFrameRate() {
        return mLastFrameRate;
    }

    public float getLastCpuRate() {
        return mLastCpuRate;
    }

    public float getLastMemoryInfo() {
        return mLastMemoryInfo;
    }

    public int getLastSkippedFrames() {
        return mLastSkippedFrames;
    }

    public float getMaxMemory() {
        return mMaxMemory;
    }
}
