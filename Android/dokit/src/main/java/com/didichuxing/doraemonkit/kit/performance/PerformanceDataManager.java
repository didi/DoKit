package com.didichuxing.doraemonkit.kit.performance;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;
import android.os.Debug;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.text.TextUtils;
import android.view.Choreographer;
import android.view.WindowManager;

import androidx.annotation.RequiresApi;

import com.didichuxing.doraemonkit.DoKit;
import com.didichuxing.doraemonkit.DoKitEnv;
import com.didichuxing.doraemonkit.config.DokitMemoryConfig;
import com.didichuxing.doraemonkit.kit.core.DoKitManager;
import com.didichuxing.doraemonkit.kit.health.AppHealthInfoUtil;
import com.didichuxing.doraemonkit.kit.health.model.AppHealthInfo;
import com.didichuxing.doraemonkit.kit.network.NetworkManager;
import com.didichuxing.doraemonkit.util.ActivityUtils;
import com.didichuxing.doraemonkit.util.AppUtils;
import com.didichuxing.doraemonkit.util.TimeUtils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

/**
 * 性能检测管理类 包括 cpu、ram、fps等
 */

public class PerformanceDataManager {
    private static final String TAG = "PerformanceDataManager";
    private static final int MAX_FRAME_RATE = 60;
    /**
     * 信息采集时间 内存和cpu
     */
    private static final int NORMAL_SAMPLING_TIME = 500;
    /**
     * fps 采集时间
     */
    private static final int FPS_SAMPLING_TIME = 1000;
    private String memoryFileName = "memory.txt";
    private String cpuFileName = "cpu.txt";
    private String fpsFileName = "fps.txt";

    //private int mLastSkippedFrames;

    private int mMaxFrameRate = MAX_FRAME_RATE;
    /**
     * cpu 百分比
     */
    private float mLastCpuRate;
    /**
     * 当前使用内存
     */
    private float mLastMemoryRate;
    /**
     * 当前的帧率
     */
    private int mLastFrameRate = mMaxFrameRate;
    private long mUpBytes;
    private long mDownBytes;
    private long mLastUpBytes;
    private long mLastDownBytes;
    /**
     * 默认的采集时间 通常为1s
     */
    private Handler mNormalHandler;
    private HandlerThread mHandlerThread;
    private float mMaxMemory;
    private Context mContext;
    private ActivityManager mActivityManager;
    private WindowManager mWindowManager;
    private RandomAccessFile mProcStatFile;
    private RandomAccessFile mAppStatFile;
    private Long mLastCpuTime;
    private Long mLastAppCpuTime;
    // 是否是8.0及其以上
    private boolean mAboveAndroidO;
    private static final int MSG_CPU = 1;
    private static final int MSG_MEMORY = 2;
    private static final int MSG_NET_FLOW = 4;
    private Handler mMainHandler = new Handler(Looper.getMainLooper());
    private FrameRateRunnable mRateRunnable = new FrameRateRunnable();

    private void executeCpuData() {
        if (mAboveAndroidO) {
            mLastCpuRate = getCpuDataForO();
            writeCpuDataIntoFile();
        } else {
            mLastCpuRate = getCPUData();
            writeCpuDataIntoFile();
        }
    }

    /**
     * 获取内存数值
     */
    private void executeMemoryData() {
        mLastMemoryRate = getMemoryData();
        writeMemoryDataIntoFile();
    }


    /**
     * 8.0以上获取cpu的方式
     *
     * @return
     */
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


    private static class Holder {
        private static PerformanceDataManager INSTANCE = new PerformanceDataManager();
    }

    private PerformanceDataManager() {
    }

    public static PerformanceDataManager getInstance() {
        return Holder.INSTANCE;
    }

    public void init() {
        mContext = DoKitEnv.requireApp().getApplicationContext();
        mActivityManager = (ActivityManager) DoKitEnv.requireApp().getSystemService(Context.ACTIVITY_SERVICE);
        mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        if (mWindowManager != null) {
            mMaxFrameRate = (int) mWindowManager.getDefaultDisplay().getRefreshRate();
        } else {
            mMaxFrameRate = MAX_FRAME_RATE;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mAboveAndroidO = true;
        }
        if (mHandlerThread == null) {
            mHandlerThread = new HandlerThread("handler-thread");
            mHandlerThread.start();
        }
        if (mNormalHandler == null) {
            //loop handler
            mNormalHandler = new Handler(mHandlerThread.getLooper()) {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    if (msg.what == MSG_CPU) {
                        if (AppUtils.isAppForeground()) {
                            executeCpuData();
                        }
                        mNormalHandler.sendEmptyMessageDelayed(MSG_CPU, NORMAL_SAMPLING_TIME);
                    } else if (msg.what == MSG_MEMORY) {
                        if (AppUtils.isAppForeground()) {
                            executeMemoryData();
                        }
                        mNormalHandler.sendEmptyMessageDelayed(MSG_MEMORY, NORMAL_SAMPLING_TIME);
                    } else if (msg.what == MSG_NET_FLOW) {
                        mLastUpBytes = NetworkManager.get().getTotalRequestSize() - mUpBytes;
                        mLastDownBytes = NetworkManager.get().getTotalResponseSize() - mDownBytes;
                        mNormalHandler.sendEmptyMessageDelayed(MSG_NET_FLOW, NORMAL_SAMPLING_TIME);
                    }
//                    else if (msg.what == MSG_SAVE_LOCAL) {
//                        saveToLocal();
//                        mNormalHandler.sendEmptyMessageDelayed(MSG_SAVE_LOCAL, NORMAL_SAMPLING_TIME);
//                    }
                }
            };
        }
    }

    private String getFilePath(Context context) {
        return context.getCacheDir() + File.separator + "doraemon/";
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void startMonitorFrameInfo() {
        DokitMemoryConfig.FPS_STATUS = true;
        //开启定时任务
        mMainHandler.postDelayed(mRateRunnable, FPS_SAMPLING_TIME);
        Choreographer.getInstance().postFrameCallback(mRateRunnable);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void stopMonitorFrameInfo() {
        DokitMemoryConfig.FPS_STATUS = false;
        Choreographer.getInstance().removeFrameCallback(mRateRunnable);
        mMainHandler.removeCallbacks(mRateRunnable);
    }

    public void startMonitorCPUInfo() {
        DokitMemoryConfig.CPU_STATUS = true;
        mNormalHandler.sendEmptyMessageDelayed(MSG_CPU, NORMAL_SAMPLING_TIME);
    }

    public void startMonitorNetFlowInfo() {
        DokitMemoryConfig.NETWORK_STATUS = true;
        mNormalHandler.sendEmptyMessageDelayed(MSG_NET_FLOW, NORMAL_SAMPLING_TIME);
    }

    public void stopMonitorNetFlowInfo() {
        DokitMemoryConfig.NETWORK_STATUS = false;
        mNormalHandler.removeMessages(MSG_NET_FLOW);
    }


    public void destroy() {
        stopMonitorMemoryInfo();
        stopMonitorCPUInfo();
        stopMonitorFrameInfo();
        if (mHandlerThread != null) {
            mHandlerThread.quit();
        }
        mHandlerThread = null;
        mNormalHandler = null;
    }


    public void stopMonitorCPUInfo() {
        DokitMemoryConfig.CPU_STATUS = false;
        mNormalHandler.removeMessages(MSG_CPU);
    }


    public void startMonitorMemoryInfo() {
        DokitMemoryConfig.RAM_STATUS = true;
        if (mMaxMemory == 0) {
            mMaxMemory = mActivityManager.getMemoryClass();
        }
        mNormalHandler.sendEmptyMessageDelayed(MSG_MEMORY, NORMAL_SAMPLING_TIME);
    }

    public void stopMonitorMemoryInfo() {
        DokitMemoryConfig.RAM_STATUS = false;
        mNormalHandler.removeMessages(MSG_MEMORY);
    }

    private void writeCpuDataIntoFile() {
        if (DoKitManager.INSTANCE.getCALLBACK() != null) {
            DoKitManager.INSTANCE.getCALLBACK().onCpuCallBack(mLastCpuRate, getCpuFilePath());
        }

        //保存cpu数据到app健康体检
        if (DoKitManager.APP_HEALTH_RUNNING) {
            addPerformanceDataInAppHealth(mLastCpuRate, PERFORMANCE_TYPE_CPU);
        }
    }

    private void writeMemoryDataIntoFile() {
        if (DoKitManager.INSTANCE.getCALLBACK() != null) {
            DoKitManager.INSTANCE.getCALLBACK().onMemoryCallBack(mLastMemoryRate, getMemoryFilePath());
        }
        //保存cpu数据到app健康体检
        if (DoKitManager.APP_HEALTH_RUNNING) {
            addPerformanceDataInAppHealth(mLastMemoryRate, PERFORMANCE_TYPE_MEMORY);
        }
    }

    private void writeFpsDataIntoFile() {
        if (DoKitManager.INSTANCE.getCALLBACK() != null) {
            DoKitManager.INSTANCE.getCALLBACK().onFpsCallBack(mLastFrameRate, getFpsFilePath());
        }
        if (DoKitManager.APP_HEALTH_RUNNING) {
            addPerformanceDataInAppHealth(mLastFrameRate > 60 ? 60 : mLastFrameRate, PERFORMANCE_TYPE_FPS);
        }
    }

    /**
     * 8.0一下获取cpu的方式
     *
     * @return
     */
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
            Debug.MemoryInfo memInfo = null;
            //28 为Android P
            if (Build.VERSION.SDK_INT > 28) {
                // 统计进程的内存信息 totalPss
                memInfo = new Debug.MemoryInfo();
                Debug.getMemoryInfo(memInfo);
            } else {
                //As of Android Q, for regular apps this method will only return information about the memory info for the processes running as the caller's uid;
                // no other process memory info is available and will be zero. Also of Android Q the sample rate allowed by this API is significantly limited, if called faster the limit you will receive the same data as the previous call.

                Debug.MemoryInfo[] memInfos = mActivityManager.getProcessMemoryInfo(new int[]{Process.myPid()});
                if (memInfos != null && memInfos.length > 0) {
                    memInfo = memInfos[0];
                }
            }
            int totalPss = 0;
            if (memInfo != null) {
                totalPss = memInfo.getTotalPss();
            }
            if (totalPss >= 0) {
                // Mem in MB
                mem = totalPss / 1024.0F;
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
                    bufferedReader.close();
                    return Float.parseFloat(lineItems[0].replace("%", ""));
                }
            }
        }
        return 0;
    }

    public String getCpuFilePath() {
        return getFilePath(mContext) + cpuFileName;
    }

    public String getMemoryFilePath() {
        return getFilePath(mContext) + memoryFileName;
    }

    public String getFpsFilePath() {
        return getFilePath(mContext) + fpsFileName;
    }


    public long getLastFrameRate() {
        return mLastFrameRate;
    }

    public float getLastCpuRate() {
        return mLastCpuRate;
    }

    public float getLastMemoryInfo() {
        return mLastMemoryRate;
    }

//    public int getLastSkippedFrames() {
//        return mLastSkippedFrames;
//    }

    public float getMaxMemory() {
        return mMaxMemory;
    }

    /**
     * 读取fps的线程
     */
    private class FrameRateRunnable implements Runnable, Choreographer.FrameCallback {
        private int totalFramesPerSecond;

        @Override
        public void run() {
            mLastFrameRate = totalFramesPerSecond;
            if (mLastFrameRate > mMaxFrameRate) {
                mLastFrameRate = mMaxFrameRate;
            }
            //保存fps数据
            if (AppUtils.isAppForeground()) {
                writeFpsDataIntoFile();
            }
            totalFramesPerSecond = 0;
            //1s中统计一次
            mMainHandler.postDelayed(this, FPS_SAMPLING_TIME);
        }

        //
        @Override
        public void doFrame(long frameTimeNanos) {
            totalFramesPerSecond++;
            Choreographer.getInstance().postFrameCallback(this);
        }

    }

    public long getLastUpBytes() {
        return mLastUpBytes;
    }

    public long getLastDownBytes() {
        return mLastDownBytes;
    }

//    private AppHealthInfo.DataBean.PerformanceBean cpuBean;
//    private AppHealthInfo.DataBean.PerformanceBean memoryBean;
//    private AppHealthInfo.DataBean.PerformanceBean fpsBean;

    /**
     * cpu
     */
    public static final int PERFORMANCE_TYPE_CPU = 1;
    /**
     * memory
     */
    public static final int PERFORMANCE_TYPE_MEMORY = 2;
    /**
     * fps
     */
    public static final int PERFORMANCE_TYPE_FPS = 3;

    /**
     * 保存cpu数据到健康体检中 统计有问题
     */
    private synchronized void addPerformanceDataInAppHealth(float performanceValue, int performanceType) {
        if (ActivityUtils.getTopActivity() == null) {
            return;
        }
        try {
            AppHealthInfo.DataBean.PerformanceBean lastPerformanceInfo = AppHealthInfoUtil.getInstance().getLastPerformanceInfo(performanceType);
            //第一次启动
            if (lastPerformanceInfo == null) {
                AppHealthInfo.DataBean.PerformanceBean performanceBean = new AppHealthInfo.DataBean.PerformanceBean();
                List<AppHealthInfo.DataBean.PerformanceBean.ValuesBean> valuesBeans = new ArrayList<>();
                valuesBeans.add(new AppHealthInfo.DataBean.PerformanceBean.ValuesBean("" + TimeUtils.getNowMills(), "" + performanceValue));
                performanceBean.setPage(ActivityUtils.getTopActivity().getClass().getCanonicalName());
                performanceBean.setPageKey(ActivityUtils.getTopActivity().toString());
                performanceBean.setValues(valuesBeans);
                if (performanceType == PERFORMANCE_TYPE_CPU) {
                    AppHealthInfoUtil.getInstance().addCPUInfo(performanceBean);
                } else if (performanceType == PERFORMANCE_TYPE_MEMORY) {
                    AppHealthInfoUtil.getInstance().addMemoryInfo(performanceBean);
                } else {
                    AppHealthInfoUtil.getInstance().addFPSInfo(performanceBean);
                }
            } else {//不是第一次启动
                String lastPageKey = lastPerformanceInfo.getPageKey();
                //同一个页面
                if (ActivityUtils.getTopActivity() != null && lastPageKey.equals(ActivityUtils.getTopActivity().toString())) {
                    List<AppHealthInfo.DataBean.PerformanceBean.ValuesBean> valuesBeans = lastPerformanceInfo.getValues();
                    int valueSize = valuesBeans.size();
                    //判断是否需要上传数据
                    //采集的点数必须在10~40之间 其中cpu 、 内存必须在20~40 因为fps 1s中采集一次
                    if (valueSize < 40) {
                        valuesBeans.add(new AppHealthInfo.DataBean.PerformanceBean.ValuesBean("" + TimeUtils.getNowMills(), "" + performanceValue));
                    }
                } else {//页面已发生变化
                    List<AppHealthInfo.DataBean.PerformanceBean.ValuesBean> lastValuesBeans = lastPerformanceInfo.getValues();
                    int valueSize = lastValuesBeans.size();
                    //先丢弃上一个页面的数据
                    if (performanceType == PERFORMANCE_TYPE_CPU && valueSize < 20) {
                        AppHealthInfoUtil.getInstance().removeLastPerformanceInfo(performanceType);
                    } else if (performanceType == PERFORMANCE_TYPE_MEMORY && valueSize < 20) {
                        AppHealthInfoUtil.getInstance().removeLastPerformanceInfo(performanceType);
                    } else if (performanceType == PERFORMANCE_TYPE_FPS && valueSize < 10) {
                        AppHealthInfoUtil.getInstance().removeLastPerformanceInfo(performanceType);
                    }

                    AppHealthInfo.DataBean.PerformanceBean performanceBean = new AppHealthInfo.DataBean.PerformanceBean();
                    List<AppHealthInfo.DataBean.PerformanceBean.ValuesBean> newValuesBeans = new ArrayList<>();
                    newValuesBeans.add(new AppHealthInfo.DataBean.PerformanceBean.ValuesBean("" + TimeUtils.getNowMills(), "" + performanceValue));
                    performanceBean.setPage(ActivityUtils.getTopActivity().getClass().getCanonicalName());
                    performanceBean.setPageKey(ActivityUtils.getTopActivity().toString());
                    performanceBean.setValues(newValuesBeans);
                    if (performanceType == PERFORMANCE_TYPE_CPU) {
                        AppHealthInfoUtil.getInstance().addCPUInfo(performanceBean);
                    } else if (performanceType == PERFORMANCE_TYPE_MEMORY) {
                        AppHealthInfoUtil.getInstance().addMemoryInfo(performanceBean);
                    } else {
                        AppHealthInfoUtil.getInstance().addFPSInfo(performanceBean);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}
