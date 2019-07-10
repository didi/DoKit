package com.didichuxing.doraemonkit.kit.common;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;
import android.os.Debug;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.Choreographer;

import com.didichuxing.doraemonkit.DoraemonKit;
import com.didichuxing.doraemonkit.config.PerformanceInfoConfig;
import com.didichuxing.doraemonkit.kit.custom.UploadMonitorInfoBean;
import com.didichuxing.doraemonkit.kit.custom.UploadMonitorItem;
import com.didichuxing.doraemonkit.kit.network.NetworkManager;
import com.didichuxing.doraemonkit.util.FileManager;
import com.didichuxing.doraemonkit.util.JsonUtil;
import com.didichuxing.doraemonkit.util.LogHelper;
import com.didichuxing.doraemonkit.util.threadpool.ThreadPoolProxyFactory;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by wanglikun on 2018/9/13.
 */

public class PerformanceDataManager {
    private static final String TAG = "PerformanceDataManager";
    private static final float SECOND_IN_NANOS = 1000000000f;
    private static final int MAX_FRAME_RATE = 60;
    private static final int NORMAL_FRAME_RATE = 1;
    private String filePath;
    private String memoryFileName = "memory.txt";
    private String cpuFileName = "cpu.txt";
    private String fpsFileName = "fps.txt";
    private String customFileName = "custom.txt"; //自定义测试页面保存的文件名称

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private int mLastFrameRate = MAX_FRAME_RATE;
    private int mLastSkippedFrames;
    private float mLastCpuRate;
    private float mLastMemoryInfo;
    private long mUpBytes;
    private long mDownBytes;
    private long mLastUpBytes;
    private long mLastDownBytes;
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
    private static final int MSG_CPU = 1;
    private static final int MSG_MEMORY = 2;
    private static final int MSG_SAVE_LOCAL = 3;
    private static final int MSG_NET_FLOW = 4;
    private UploadMonitorInfoBean mUploadMonitorBean;
    private boolean mUploading;
    private Handler mMainHandler = new Handler(Looper.getMainLooper());
    private FrameRateRunnable mRateRunnable = new FrameRateRunnable();

    private void executeCpuData() {
        LogHelper.d(TAG, "current thread name is ==" + Thread.currentThread().getName());
        if (mAboveAndroidO) {
            mLastCpuRate = getCpuDataForO();
            LogHelper.d(TAG, "cpu info is =" + mLastCpuRate);
            writeCpuDataIntoFile();
        } else {
            mLastCpuRate = getCPUData();
            LogHelper.d(TAG, "cpu info is =" + mLastCpuRate);
            writeCpuDataIntoFile();
        }
    }

    private float getCpuDataForO() {
        java.lang.Process process = null;
        try {
            process = Runtime.getRuntime().exec("top -n 1");
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            int cpuIndex = -1;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (TextUtils.isEmpty(line)) {
                    continue;
                }
                int tempIndex = getCPUIndex(line);
                if (tempIndex != -1) {
                    cpuIndex = tempIndex;
                    continue;
                }
                if (line.startsWith(String.valueOf(Process.myPid()))) {
                    if (cpuIndex == -1) {
                        continue;
                    }
                    String[] param = line.split("\\s+");
                    if (param.length <= cpuIndex) {
                        continue;
                    }
                    String cpu = param[cpuIndex];
                    if (cpu.endsWith("%")) {
                        cpu = cpu.substring(0, cpu.lastIndexOf("%"));
                    }
                    float rate = Float.parseFloat(cpu) / Runtime.getRuntime().availableProcessors();
                    return rate;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (process != null) {
                process.destroy();
            }
        }
        return 0;
    }

    private int getCPUIndex(String line) {
        if (line.contains("CPU")) {
            String[] titles = line.split("\\s+");
            for (int i = 0; i < titles.length; i++) {
                if (titles[i].contains("CPU")) {
                    return i;
                }
            }
        }
        return -1;
    }

    private void executeMemoryData() {
        mLastMemoryInfo = getMemoryData();
        LogHelper.d(TAG, "memory info is =" + mLastMemoryInfo);
        writeMemoryDataIntoFile();
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
        filePath = getFilePath(context);
        mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mAboveAndroidO = true;
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
                        executeCpuData();
                        mHandler.sendEmptyMessageDelayed(MSG_CPU, NORMAL_FRAME_RATE * 1000);
                    } else if (msg.what == MSG_MEMORY) {
                        executeMemoryData();
                        mHandler.sendEmptyMessageDelayed(MSG_MEMORY, NORMAL_FRAME_RATE * 1000);
                    } else if (msg.what == MSG_NET_FLOW){
                        mLastUpBytes = NetworkManager.get().getTotalRequestSize() - mUpBytes;
                        mLastDownBytes = NetworkManager.get().getTotalResponseSize() - mDownBytes;
                        mHandler.sendEmptyMessageDelayed(MSG_NET_FLOW, NORMAL_FRAME_RATE * 1000);
                    } else if (msg.what == MSG_SAVE_LOCAL){
                        saveToLocal();
                        mHandler.sendEmptyMessageDelayed(MSG_SAVE_LOCAL, NORMAL_FRAME_RATE * 1000);
                    }
                }
            };
        }
    }

    private String getFilePath(Context context) {
        boolean hasExternalStorage = Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
        if (hasExternalStorage) {
            return context.getExternalFilesDir(null).getAbsolutePath() + "/doraemon/";
        } else {
            return Environment.getExternalStorageDirectory().getAbsolutePath() + "/doraemon/";
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void startMonitorFrameInfo() {
        mMainHandler.postDelayed(mRateRunnable, DateUtils.SECOND_IN_MILLIS);
        Choreographer.getInstance().postFrameCallback(mRateRunnable);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void stopMonitorFrameInfo() {
        Choreographer.getInstance().removeFrameCallback(mRateRunnable);
        mMainHandler.removeCallbacks(mRateRunnable);
    }

    public void startMonitorCPUInfo() {
        mHandler.sendEmptyMessageDelayed(MSG_CPU, NORMAL_FRAME_RATE * 1000);
    }

    public void startMonitorNetFlowInfo() {
        mHandler.sendEmptyMessageDelayed(MSG_NET_FLOW, NORMAL_FRAME_RATE * 1000);
    }

    public void stopMonitorNetFlowInfo() {
        mHandler.removeMessages(MSG_NET_FLOW);
    }

    public void startUploadMonitorData() {
        mUploading = true;
        if (mUploadMonitorBean != null) {
            mUploadMonitorBean = null;
        }
        if (PerformanceInfoConfig.isFPSOpen(mContext)) {
            startMonitorFrameInfo();
        }
        if (PerformanceInfoConfig.isCPUOpen(mContext)) {
            startMonitorCPUInfo();
        }
        if (PerformanceInfoConfig.isMemoryOpen(mContext)) {
            startMonitorMemoryInfo();
        }
        if (PerformanceInfoConfig.isTrafficOpen(mContext)) {
            NetworkManager.get().startMonitor();
            startMonitorNetFlowInfo();
        }
        mHandler.sendEmptyMessageDelayed(MSG_SAVE_LOCAL, NORMAL_FRAME_RATE * 1000);
    }

    public void stopUploadMonitorData(){
        mUploading = false;
        mHandler.removeMessages(MSG_SAVE_LOCAL);
        uploadDataToLocalFile();
        stopMonitorFrameInfo();
        stopMonitorCPUInfo();
        stopMonitorMemoryInfo();
        stopMonitorNetFlowInfo();
        NetworkManager.get().stopMonitor();
    }

    public boolean isUploading(){
        return mUploading;
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

    private void saveToLocal() {
        if (mUploadMonitorBean == null) {
            mUploadMonitorBean = new UploadMonitorInfoBean();
            mUploadMonitorBean.appName = mContext.getPackageName();
            if(mUploadMonitorBean.performanceArray == null){
                mUploadMonitorBean.performanceArray = new ArrayList<>();
            }
        }
        NetworkManager networkManager = NetworkManager.get();
        long upSize = networkManager.getTotalRequestSize();
        long downSize = networkManager.getTotalResponseSize();

        UploadMonitorItem info = new UploadMonitorItem();
        info.cpu = mLastCpuRate;
        info.fps = mLastFrameRate;
        info.memory = mLastMemoryInfo;
        info.upFlow = mLastUpBytes;
        info.downFlow = mLastDownBytes;
        mUpBytes = upSize;
        mDownBytes = downSize;
        info.timestamp = System.currentTimeMillis();

        String pageName = "unkown";
        if (DoraemonKit.getCurrentResumedActivity() != null) {
            pageName = DoraemonKit.getCurrentResumedActivity().getLocalClassName();
        }
        info.page = pageName;
        mUploadMonitorBean.performanceArray.add(info);
    }

    private void uploadDataToLocalFile() {
        ThreadPoolProxyFactory.getThreadPoolProxy().execute(new Runnable() {
            @Override
            public void run() {
                FileManager.writeTxtToFile(JsonUtil.jsonFromObject(mUploadMonitorBean), filePath, customFileName);
            }
        });
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
            LogHelper.e(TAG,"getCPUData fail: "+e.toString());
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
            LogHelper.e(TAG,"getMemoryData fail: "+e.toString());
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

    public String getCustomFilePath() {
        return filePath + customFileName;
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

    private class FrameRateRunnable implements Runnable, Choreographer.FrameCallback {
        private int totalFramesPerSecond;

        @Override
        public void run() {
            mLastFrameRate = totalFramesPerSecond;
            if (mLastFrameRate > MAX_FRAME_RATE) {
                mLastFrameRate = MAX_FRAME_RATE;
            }
            mLastSkippedFrames = MAX_FRAME_RATE - mLastFrameRate;
            totalFramesPerSecond = 0;
            mMainHandler.postDelayed(this, DateUtils.SECOND_IN_MILLIS);
        }

        @Override
        public void doFrame(long frameTimeNanos) {
            totalFramesPerSecond++;
            Choreographer.getInstance().postFrameCallback(this);
            writeFpsDataIntoFile();
        }
    }

    public long getLastUpBytes() {
        return mLastUpBytes;
    }

    public long getLastDownBytes() {
        return mLastDownBytes;
    }
}
