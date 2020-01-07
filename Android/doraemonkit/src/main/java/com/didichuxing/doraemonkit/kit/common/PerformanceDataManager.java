package com.didichuxing.doraemonkit.kit.common;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;
import android.os.Debug;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.Choreographer;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.TimeUtils;
import com.didichuxing.doraemonkit.DoraemonKit;
import com.didichuxing.doraemonkit.config.PerformanceMemoryInfoConfig;
import com.didichuxing.doraemonkit.config.PerformanceSpInfoConfig;
import com.didichuxing.doraemonkit.constant.DokitConstant;
import com.didichuxing.doraemonkit.kit.custom.UploadMonitorInfoBean;
import com.didichuxing.doraemonkit.kit.custom.UploadMonitorItem;
import com.didichuxing.doraemonkit.kit.health.AppHealthInfoUtil;
import com.didichuxing.doraemonkit.kit.health.model.AppHealthInfo;
import com.didichuxing.doraemonkit.kit.network.NetworkManager;
import com.didichuxing.doraemonkit.util.FileManager;
import com.didichuxing.doraemonkit.util.JsonUtil;
import com.didichuxing.doraemonkit.util.LogHelper;
import com.didichuxing.doraemonkit.util.threadpool.ThreadPoolProxyFactory;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 性能检测管理类 包括 cpu、ram、fps等
 */

public class PerformanceDataManager {
    private static final String TAG = "PerformanceDataManager";
    private static final int MAX_FRAME_RATE = 60;
    /**
     * 信息采集时间
     */
    private static final float NORMAL_FRAME_RATE = 0.5f;
    private String memoryFileName = "memory.txt";
    private String cpuFileName = "cpu.txt";
    private String fpsFileName = "fps.txt";
    /**
     * 自定义测试页面保存的文件名称
     */
    private String customFileName = "custom.txt";

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private int mLastFrameRate = MAX_FRAME_RATE;
    private int mLastSkippedFrames;
    /**
     * cpu 百分比
     */
    private float mLastCpuRate;
    /**
     * 内存百分比
     */
    private float mLastMemoryRate;
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

    /**
     * 获取内存数值
     */
    private void executeMemoryData() {
        mLastMemoryRate = getMemoryData();
        LogHelper.d(TAG, "memory info is =" + mLastMemoryRate);
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
        mContext = DoraemonKit.APPLICATION.getApplicationContext();
        mActivityManager = (ActivityManager) DoraemonKit.APPLICATION.getSystemService(Context.ACTIVITY_SERVICE);
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
                        executeCpuData();
                        mNormalHandler.sendEmptyMessageDelayed(MSG_CPU, (long) (NORMAL_FRAME_RATE * 1000));
                    } else if (msg.what == MSG_MEMORY) {
                        executeMemoryData();
                        mNormalHandler.sendEmptyMessageDelayed(MSG_MEMORY, (long) (NORMAL_FRAME_RATE * 1000));
                    } else if (msg.what == MSG_NET_FLOW) {
                        mLastUpBytes = NetworkManager.get().getTotalRequestSize() - mUpBytes;
                        mLastDownBytes = NetworkManager.get().getTotalResponseSize() - mDownBytes;
                        mNormalHandler.sendEmptyMessageDelayed(MSG_NET_FLOW, (long) (NORMAL_FRAME_RATE * 1000));
                    } else if (msg.what == MSG_SAVE_LOCAL) {
                        saveToLocal();
                        mNormalHandler.sendEmptyMessageDelayed(MSG_SAVE_LOCAL, (long) (NORMAL_FRAME_RATE * 1000));
                    }
                }
            };
        }
    }

    private String getFilePath(Context context) {
        return context.getCacheDir() + File.separator + "doraemon/";
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void startMonitorFrameInfo() {
        PerformanceMemoryInfoConfig.FPS_STATUS = true;
        //开启定时任务
        mMainHandler.postDelayed(mRateRunnable, DateUtils.SECOND_IN_MILLIS);
        Choreographer.getInstance().postFrameCallback(mRateRunnable);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void stopMonitorFrameInfo() {
        PerformanceMemoryInfoConfig.FPS_STATUS = false;
        Choreographer.getInstance().removeFrameCallback(mRateRunnable);
        mMainHandler.removeCallbacks(mRateRunnable);
    }

    public void startMonitorCPUInfo() {
        PerformanceMemoryInfoConfig.CPU_STATUS = true;
        mNormalHandler.sendEmptyMessageDelayed(MSG_CPU, (long) (NORMAL_FRAME_RATE * 1000));
    }

    public void startMonitorNetFlowInfo() {
        PerformanceMemoryInfoConfig.NETWORK_STATUS = true;
        mNormalHandler.sendEmptyMessageDelayed(MSG_NET_FLOW, (long) (NORMAL_FRAME_RATE * 1000));
    }

    public void stopMonitorNetFlowInfo() {
        PerformanceMemoryInfoConfig.NETWORK_STATUS = false;
        mNormalHandler.removeMessages(MSG_NET_FLOW);
    }

    public void startUploadMonitorData() {
        mUploading = true;
        if (mUploadMonitorBean != null) {
            mUploadMonitorBean = null;
        }
        if (PerformanceSpInfoConfig.isFPSOpen(mContext)) {
            startMonitorFrameInfo();
        }
        if (PerformanceSpInfoConfig.isCPUOpen(mContext)) {
            startMonitorCPUInfo();
        }
        if (PerformanceSpInfoConfig.isMemoryOpen(mContext)) {
            startMonitorMemoryInfo();
        }
        if (PerformanceSpInfoConfig.isTrafficOpen(mContext)) {
            NetworkManager.get().startMonitor();
            startMonitorNetFlowInfo();
        }
        mNormalHandler.sendEmptyMessageDelayed(MSG_SAVE_LOCAL, (long) (NORMAL_FRAME_RATE * 1000));
    }

    public void stopUploadMonitorData() {
        mUploading = false;
        mNormalHandler.removeMessages(MSG_SAVE_LOCAL);
        uploadDataToLocalFile();
        stopMonitorFrameInfo();
        stopMonitorCPUInfo();
        stopMonitorMemoryInfo();
        stopMonitorNetFlowInfo();
        NetworkManager.get().stopMonitor();
    }

    public boolean isUploading() {
        return mUploading;
    }

    public void stopMonitorCPUInfo() {
        PerformanceMemoryInfoConfig.CPU_STATUS = false;
        mNormalHandler.removeMessages(MSG_CPU);
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

    private void saveToLocal() {
        if (mUploadMonitorBean == null) {
            mUploadMonitorBean = new UploadMonitorInfoBean();
            mUploadMonitorBean.appName = mContext.getPackageName();
            if (mUploadMonitorBean.performanceArray == null) {
                mUploadMonitorBean.performanceArray = new ArrayList<>();
            }
        }
        NetworkManager networkManager = NetworkManager.get();
        long upSize = networkManager.getTotalRequestSize();
        long downSize = networkManager.getTotalResponseSize();

        UploadMonitorItem info = new UploadMonitorItem();
        info.cpu = mLastCpuRate;
        info.fps = mLastFrameRate;
        info.memory = mLastMemoryRate;
        info.upFlow = mLastUpBytes;
        info.downFlow = mLastDownBytes;
        mUpBytes = upSize;
        mDownBytes = downSize;
        info.timestamp = System.currentTimeMillis();

        String pageName = "unkown";
        if (ActivityUtils.getTopActivity() != null) {
            pageName = ActivityUtils.getTopActivity().getLocalClassName();
        }
        info.page = pageName;
        mUploadMonitorBean.performanceArray.add(info);
    }

    private void uploadDataToLocalFile() {
        ThreadPoolProxyFactory.getThreadPoolProxy().execute(new Runnable() {
            @Override
            public void run() {
                FileManager.writeTxtToFile(JsonUtil.jsonFromObject(mUploadMonitorBean), getFilePath(mContext), customFileName);
            }
        });
    }

    public void startMonitorMemoryInfo() {
        PerformanceMemoryInfoConfig.RAM_STATUS = true;
        if (mMaxMemory == 0) {
            mMaxMemory = mActivityManager.getMemoryClass();
        }
        mNormalHandler.sendEmptyMessageDelayed(MSG_MEMORY, (long) (NORMAL_FRAME_RATE * 1000));
    }

    public void stopMonitorMemoryInfo() {
        PerformanceMemoryInfoConfig.RAM_STATUS = false;
        mNormalHandler.removeMessages(MSG_MEMORY);
    }

    private void writeCpuDataIntoFile() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(mLastCpuRate);
        stringBuilder.append(" ");
        stringBuilder.append(simpleDateFormat.format(new Date(System.currentTimeMillis())));
        //保存cpu数据到app健康体检
        if (DokitConstant.APP_HEALTH_RUNNING) {
            addPerformanceDataInAppHealth(mLastCpuRate, PERFORMANCE_TYPE_CPU);
        }
        FileManager.writeTxtToFile(stringBuilder.toString(), getFilePath(mContext), cpuFileName);
    }

    private void writeMemoryDataIntoFile() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(mLastMemoryRate);
        stringBuilder.append(" ");
        stringBuilder.append(simpleDateFormat.format(new Date(System.currentTimeMillis())));
        //保存cpu数据到app健康体检
        if (DokitConstant.APP_HEALTH_RUNNING) {
            addPerformanceDataInAppHealth(mLastMemoryRate, PERFORMANCE_TYPE_MEMORY);
        }
        FileManager.writeTxtToFile(stringBuilder.toString(), getFilePath(mContext), memoryFileName);
    }

    private void writeFpsDataIntoFile() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(mLastFrameRate);
        stringBuilder.append(" ");
        stringBuilder.append(simpleDateFormat.format(new Date(System.currentTimeMillis())));
        if (DokitConstant.APP_HEALTH_RUNNING) {
            addPerformanceDataInAppHealth(mLastFrameRate, PERFORMANCE_TYPE_FPS);
        }
        FileManager.writeTxtToFile(stringBuilder.toString(), getFilePath(mContext), fpsFileName);
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
            LogHelper.e(TAG, "getCPUData fail: " + e.toString());
        }
        return value;
    }

    private float getMemoryData() {
        float mem = 0.0F;
        try {
            Debug.MemoryInfo memInfo = null;
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
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

            int totalPss = memInfo.getTotalPss();
            if (totalPss >= 0) {
                // Mem in MB
                mem = totalPss / 1024.0F;
            }
        } catch (
                Exception e) {
            LogHelper.e(TAG, "getMemoryData fail: " + e.toString());
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
        return getFilePath(mContext) + cpuFileName;
    }

    public String getMemoryFilePath() {
        return getFilePath(mContext) + memoryFileName;
    }

    public String getFpsFilePath() {
        return getFilePath(mContext) + fpsFileName;
    }

    public String getCustomFilePath() {
        return getFilePath(mContext) + customFileName;
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

    public int getLastSkippedFrames() {
        return mLastSkippedFrames;
    }

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

    private AppHealthInfo.DataBean.PerformanceBean cpuBean;
    private AppHealthInfo.DataBean.PerformanceBean memoryBean;
    private AppHealthInfo.DataBean.PerformanceBean fpsBean;

    /**
     * cpu
     */
    private static final int PERFORMANCE_TYPE_CPU = 1;
    /**
     * memory
     */
    private static final int PERFORMANCE_TYPE_MEMORY = 2;
    /**
     * fps
     */
    private static final int PERFORMANCE_TYPE_FPS = 3;

    /**
     * 保存cpu数据到健康体检中
     */
    private void addPerformanceDataInAppHealth(float value, int performanceType) {
        try {
            AppHealthInfo.DataBean.PerformanceBean performanceBean = null;
            if (performanceType == PERFORMANCE_TYPE_CPU) {
                performanceBean = cpuBean;
            } else if (performanceType == PERFORMANCE_TYPE_MEMORY) {
                performanceBean = memoryBean;
            } else if (performanceType == PERFORMANCE_TYPE_FPS) {
                performanceBean = fpsBean;
            }

            if (performanceBean == null) {
                performanceBean = new AppHealthInfo.DataBean.PerformanceBean();
                List<AppHealthInfo.DataBean.PerformanceBean.ValuesBean> valuesBeans = new ArrayList<>();
                valuesBeans.add(new AppHealthInfo.DataBean.PerformanceBean.ValuesBean("" + TimeUtils.getNowMills(), "" + value));
                performanceBean.setPage(ActivityUtils.getTopActivity().getClass().getCanonicalName());
                performanceBean.setValues(valuesBeans);
                //重新赋值
                if (performanceType == PERFORMANCE_TYPE_CPU) {
                    cpuBean = performanceBean;
                } else if (performanceType == PERFORMANCE_TYPE_MEMORY) {
                    memoryBean = performanceBean;
                } else if (performanceType == PERFORMANCE_TYPE_FPS) {
                    fpsBean = performanceBean;
                }
            } else {
                //判断是否还处于统一页面
                String nextPage = performanceBean.getPage();
                //如果不是同一个页面则丢弃当前页面的数据
                if (!nextPage.equals(ActivityUtils.getTopActivity().getClass().getCanonicalName())) {
                    if (performanceType == PERFORMANCE_TYPE_CPU) {
                        cpuBean = null;
                    } else if (performanceType == PERFORMANCE_TYPE_MEMORY) {
                        memoryBean = null;
                    } else if (performanceType == PERFORMANCE_TYPE_FPS) {
                        fpsBean = null;
                    }
                    return;
                }
                //添加数据
                List<AppHealthInfo.DataBean.PerformanceBean.ValuesBean> valuesBeans = performanceBean.getValues();
                if (valuesBeans.size() < 20) {
                    valuesBeans.add(new AppHealthInfo.DataBean.PerformanceBean.ValuesBean("" + TimeUtils.getNowMills(), "" + value));
                } else {
                    return;
                }
            }


            //保存到AppHealth中
            List<AppHealthInfo.DataBean.PerformanceBean.ValuesBean> valuesBeans = performanceBean.getValues();

            //判断是否采集到了20个点
            if (valuesBeans.size() == 20) {
                AppHealthInfo.DataBean.PerformanceBean realPerformBean = new AppHealthInfo.DataBean.PerformanceBean();
                realPerformBean.setValues(performanceBean.getValues());
                realPerformBean.setPage(performanceBean.getPage());
                if (performanceType == PERFORMANCE_TYPE_CPU) {
                    AppHealthInfoUtil.getInstance().addCPUInfo(realPerformBean);
                } else if (performanceType == PERFORMANCE_TYPE_MEMORY) {
                    AppHealthInfoUtil.getInstance().addMemoryInfo(realPerformBean);
                } else if (performanceType == PERFORMANCE_TYPE_FPS) {
                    AppHealthInfoUtil.getInstance().addFPSInfo(realPerformBean);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}
