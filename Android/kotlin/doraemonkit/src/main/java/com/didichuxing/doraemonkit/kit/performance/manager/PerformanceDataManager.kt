package com.didichuxing.doraemonkit.kit.performance.manager

import android.app.ActivityManager
import android.content.Context
import android.os.*
import android.text.TextUtils
import android.view.Choreographer
import androidx.annotation.RequiresApi
import com.blankj.utilcode.util.AppUtils
import com.didichuxing.doraemonkit.DoraemonKit
import com.didichuxing.doraemonkit.config.DokitMemoryConfig
import com.didichuxing.doraemonkit.constant.DokitConstant
import java.io.*
import java.lang.Process

/**
 *
 * Desc:性能检测管理类 包括 cpu、ram、fps等
 * <p>
 * Date: 2020-06-09
 * Company:
 * Updater:
 * Update Time:
 * Update Comments:
 *
 * Author: pengyushan
 */
class PerformanceDataManager private constructor() {
    private val memoryFileName = "memory.txt"
    private val cpuFileName = "cpu.txt"
    private val fpsFileName = "fps.txt"
    /**
     * cpu 百分比
     */
    var lastCpuRate = 0f

    /**
     * 当前使用内存
     */
    var lastMemoryInfo = 0f

    /**
     * 当前的帧率
     */
    private var mLastFrameRate = MAX_FRAME_RATE
    private val mUpBytes: Long = 0
    private val mDownBytes: Long = 0
    val lastUpBytes: Long = 0
    val lastDownBytes: Long = 0

    /**
     * 默认的采集时间 通常为1s
     */
    private var mNormalHandler: Handler? = null
    private var mHandlerThread: HandlerThread? = null

    var maxMemory = 0f
    private var mContext: Context? = null
    private var mActivityManager: ActivityManager? = null
    private var mProcStatFile: RandomAccessFile? = null
    private var mAppStatFile: RandomAccessFile? = null
    private var mLastCpuTime: Long? = null
    private var mLastAppCpuTime: Long? = null

    // 是否是8.0及其以上
    private var mAboveAndroidO = false
    private val mMainHandler = Handler(Looper.getMainLooper())
    private val mRateRunnable = FrameRateRunnable()
    private fun executeCpuData() {
        if (mAboveAndroidO) {
            lastCpuRate = getCpuData()
            writeCpuDataIntoFile()
        } else {
            lastCpuRate = getCpuDataAndroidVersion8()
            writeCpuDataIntoFile()
        }
    }

    /**
     * 获取内存数值
     */
    private fun executeMemoryData() {
        lastMemoryInfo = getMemoryData()
        writeMemoryDataIntoFile()
    }

    /**
     * 8.0以上获取cpu的方式
     *
     * @return
     */
    private fun getCpuData():Float{
        var process: Process? = null
        try {
            process = Runtime.getRuntime().exec("top -n 1")
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            var line: String
            var cpuIndex = -1
            while (reader.readLine().apply { line = this }  != null) {
                line = line.trim { it <= ' ' }
                if (TextUtils.isEmpty(line)) {
                    continue
                }
                val tempIndex = getCPUIndex(line)
                if (tempIndex != -1) {
                    cpuIndex = tempIndex
                    continue
                }
                if (line.startsWith(android.os.Process.myPid().toString())) {
                    if (cpuIndex == -1) {
                        continue
                    }
                    val param = line.split("\\s+".toRegex()).toTypedArray()
                    if (param.size <= cpuIndex) {
                        continue
                    }
                    var cpu = param[cpuIndex]
                    if (cpu.endsWith("%")) {
                        cpu = cpu.substring(0, cpu.lastIndexOf("%"))
                    }
                    return cpu.toFloat() / Runtime.getRuntime().availableProcessors()
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            process?.destroy()
        }
        return 0F
    }

    private fun getCPUIndex(line: String): Int {
        if (line.contains("CPU")) {
            val titles = line.split("\\s+".toRegex()).toTypedArray()
            for (i in titles.indices) {
                if (titles[i].contains("CPU")) {
                    return i
                }
            }
        }
        return -1
    }

    private object Holder {
        val INSTANCE = PerformanceDataManager()
    }

    fun init() {
        mContext = DoraemonKit.APPLICATION!!.applicationContext
        mActivityManager = DoraemonKit.APPLICATION!!.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mAboveAndroidO = true
        }
        if (mHandlerThread == null) {
            mHandlerThread = HandlerThread("handler-thread")
            mHandlerThread!!.start()
        }
        if (mNormalHandler == null) {
            //loop handler
            mNormalHandler = object : Handler(mHandlerThread!!.looper) {
                override fun handleMessage(msg: Message) {
                    super.handleMessage(msg)
                    if (msg.what == MSG_CPU) {
                        if (AppUtils.isAppForeground()) {
                            executeCpuData()
                        }
                        mNormalHandler!!.sendEmptyMessageDelayed(MSG_CPU, NORMAL_SAMPLING_TIME.toLong())
                    } else if (msg.what == MSG_MEMORY) {
                        if (AppUtils.isAppForeground()) {
                            executeMemoryData()
                        }
                        mNormalHandler!!.sendEmptyMessageDelayed(MSG_MEMORY, NORMAL_SAMPLING_TIME.toLong())
                    }
                    //                    else if (msg.what == MSG_SAVE_LOCAL) {
//                        saveToLocal();
//                        mNormalHandler.sendEmptyMessageDelayed(MSG_SAVE_LOCAL, NORMAL_SAMPLING_TIME);
//                    }
                }
            }
        }
    }

    private fun getFilePath(context: Context?): String {
        return context?.cacheDir.toString() + File.separator + "doraemon/"
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    fun startMonitorFrameInfo() {
        DokitMemoryConfig.FPS_STATUS = true
        //开启定时任务
        mMainHandler.postDelayed(mRateRunnable, FPS_SAMPLING_TIME.toLong())
        Choreographer.getInstance().postFrameCallback(mRateRunnable)
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    fun stopMonitorFrameInfo() {
        DokitMemoryConfig.FPS_STATUS = false
        Choreographer.getInstance().removeFrameCallback(mRateRunnable)
        mMainHandler.removeCallbacks(mRateRunnable)
    }

    fun startMonitorCPUInfo() {
        DokitMemoryConfig.CPU_STATUS = true
        mNormalHandler!!.sendEmptyMessageDelayed(MSG_CPU, NORMAL_SAMPLING_TIME.toLong())
    }

    fun startMonitorNetFlowInfo() {
        DokitMemoryConfig.NETWORK_STATUS = true
        mNormalHandler!!.sendEmptyMessageDelayed(MSG_NET_FLOW, NORMAL_SAMPLING_TIME.toLong())
    }

    fun stopMonitorNetFlowInfo() {
        DokitMemoryConfig.NETWORK_STATUS = false
        mNormalHandler!!.removeMessages(MSG_NET_FLOW)
    }

    fun destroy() {
        stopMonitorMemoryInfo()
        stopMonitorCPUInfo()
        stopMonitorFrameInfo()
        if (mHandlerThread != null) {
            mHandlerThread!!.quit()
        }
        mHandlerThread = null
        mNormalHandler = null
    }

    fun stopMonitorCPUInfo() {
        DokitMemoryConfig.CPU_STATUS = false
        mNormalHandler!!.removeMessages(MSG_CPU)
    }

    fun startMonitorMemoryInfo() {
        DokitMemoryConfig.RAM_STATUS = true
        if (maxMemory == 0f) {
            maxMemory = mActivityManager!!.memoryClass.toFloat()
        }
        mNormalHandler!!.sendEmptyMessageDelayed(MSG_MEMORY, NORMAL_SAMPLING_TIME.toLong())
    }

    fun stopMonitorMemoryInfo() {
        DokitMemoryConfig.RAM_STATUS = false
        mNormalHandler!!.removeMessages(MSG_MEMORY)
    }

    private fun writeCpuDataIntoFile() {

        //保存cpu数据到app健康体检
        if (DokitConstant.APP_HEALTH_RUNNING) {
            addPerformanceDataInAppHealth(lastCpuRate, PERFORMANCE_TYPE_CPU)
        }
    }

    private fun writeMemoryDataIntoFile() {

        //保存cpu数据到app健康体检
        if (DokitConstant.APP_HEALTH_RUNNING) {
            addPerformanceDataInAppHealth(lastMemoryInfo, PERFORMANCE_TYPE_MEMORY)
        }
    }

    private fun writeFpsDataIntoFile() {
        if (DokitConstant.APP_HEALTH_RUNNING) {
//            addPerformanceDataInAppHealth(if (mLastFrameRate > 60) 60 else mLastFrameRate.toFloat(), PERFORMANCE_TYPE_FPS)
        }
    }

    /**
     *
     * Desc:获取8.0以下设备的cpu信息
     * <p>
     * Author: pengyushan
     * Date: 2020-06-09
     * @return Float
     */
    private fun getCpuDataAndroidVersion8():Float{
        val cpuTime: Long
        val appTime: Long
        var value = 0.0f
        try {
            if (mProcStatFile == null || mAppStatFile == null) {
                mProcStatFile = RandomAccessFile("/proc/stat", "r")
                mAppStatFile = RandomAccessFile("/proc/" + android.os.Process.myPid() + "/stat", "r")
            } else {
                mProcStatFile!!.seek(0L)
                mAppStatFile!!.seek(0L)
            }
            val procStatString = mProcStatFile!!.readLine()
            val appStatString = mAppStatFile!!.readLine()
            val procStats = procStatString.split(" ".toRegex()).toTypedArray()
            val appStats = appStatString.split(" ".toRegex()).toTypedArray()
            cpuTime = procStats[2].toLong() + procStats[3].toLong() + procStats[4].toLong() + procStats[5].toLong() + procStats[6].toLong() + procStats[7].toLong() + procStats[8].toLong()
            appTime = appStats[13].toLong() + appStats[14].toLong()
            if (mLastCpuTime == null && mLastAppCpuTime == null) {
                mLastCpuTime = cpuTime
                mLastAppCpuTime = appTime
                return value
            }
            value = (appTime - mLastAppCpuTime!!).toFloat() / (cpuTime - mLastCpuTime!!).toFloat() * 100f
            mLastCpuTime = cpuTime
            mLastAppCpuTime = appTime
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return value
    }

    /**
     *
     * Desc:获取内存信息
     * <p>
     * Author: pengyushan
     * Date: 2020-06-09
     * @return Float
     */
    private fun getMemoryData():Float{
        var mem = 0.0f
        try {
            var memInfo: Debug.MemoryInfo? = null
            //28 为Android P
            if (Build.VERSION.SDK_INT > 28) {
                // 统计进程的内存信息 totalPss
                memInfo = Debug.MemoryInfo()
                Debug.getMemoryInfo(memInfo)
            } else {
                //As of Android Q, for regular apps this method will only return information about the memory info for the processes running as the caller's uid;
                // no other process memory info is available and will be zero. Also of Android Q the sample rate allowed by this API is significantly limited, if called faster the limit you will receive the same data as the previous call.
                val memInfos = mActivityManager!!.getProcessMemoryInfo(intArrayOf(android.os.Process.myPid()))
                if (memInfos != null && memInfos.size > 0) {
                    memInfo = memInfos[0]
                }
            }
            val totalPss = memInfo!!.totalPss
            if (totalPss >= 0) {
                // Mem in MB
                mem = totalPss / 1024.0f
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return mem
    }


    @Throws(IOException::class)
    private fun parseMemoryData(data: String): Float {
        val bufferedReader = BufferedReader(InputStreamReader(ByteArrayInputStream(data.toByteArray())))
        var line: String
        while (bufferedReader.readLine().also { line = it } != null) {
            line = line.trim { it <= ' ' }
            if (line.contains("Permission Denial")) {
                break
            } else {
                val lineItems = line.split("\\s+".toRegex()).toTypedArray()
                if (lineItems.size > 1) {
                    var result = lineItems[0]
                    bufferedReader.close()
                    if (!TextUtils.isEmpty(result) && result.contains("K:")) {
                        result = result.replace("K:", "")
                        if (result.contains(",")) {
                            result = result.replace(",", ".")
                        }
                    }
                    // Mem in MB
                    return result.toFloat() / 1024
                }
            }
        }
        return 0F
    }

    @Throws(IOException::class)
    private fun parseCPUData(data: String): Float {
        val bufferedReader = BufferedReader(InputStreamReader(ByteArrayInputStream(data.toByteArray())))
        var line: String
        while (bufferedReader.readLine().also { line = it } != null) {
            line = line.trim { it <= ' ' }
            if (line.contains("Permission Denial")) {
                break
            } else {
                val lineItems = line.split("\\s+".toRegex()).toTypedArray()
                if (lineItems.size > 1) {
                    bufferedReader.close()
                    return lineItems[0].replace("%", "").toFloat()
                }
            }
        }
        return 0F
    }

    val cpuFilePath = getFilePath(mContext) + cpuFileName

    val memoryFilePath = getFilePath(mContext) + memoryFileName

    val fpsFilePath = getFilePath(mContext) + fpsFileName

    val lastFrameRate = mLastFrameRate.toFloat()

    /**
     * 读取fps的线程
     */
    private inner class FrameRateRunnable : Runnable, Choreographer.FrameCallback {
        private var totalFramesPerSecond = 0
        override fun run() {
            mLastFrameRate = totalFramesPerSecond
            if (mLastFrameRate > MAX_FRAME_RATE) {
                mLastFrameRate = MAX_FRAME_RATE
            }
            //保存fps数据
            if (AppUtils.isAppForeground()) {
                writeFpsDataIntoFile()
            }
            totalFramesPerSecond = 0
            //1s中统计一次
            mMainHandler.postDelayed(this, FPS_SAMPLING_TIME.toLong())
        }

        //Choreographer 回调
        override fun doFrame(frameTimeNanos: Long) {
            totalFramesPerSecond++
            Choreographer.getInstance().postFrameCallback(this)
        }
    }

    /**
     * 保存cpu数据到健康体检中 统计有问题
     */
    @Synchronized
    private fun addPerformanceDataInAppHealth(performanceValue: Float, performanceType: Int) {

    }

    companion object {
        private const val TAG = "PerformanceDataManager"
        private const val MAX_FRAME_RATE = 60

        /**
         * 信息采集时间 内存和cpu
         */
        private const val NORMAL_SAMPLING_TIME = 500

        /**
         * fps 采集时间
         */
        private const val FPS_SAMPLING_TIME = 1000
        private const val MSG_CPU = 1
        private const val MSG_MEMORY = 2
        private const val MSG_NET_FLOW = 4
        val instance = Holder.INSTANCE
        //    private AppHealthInfo.DataBean.PerformanceBean cpuBean;
        //    private AppHealthInfo.DataBean.PerformanceBean memoryBean;
        //    private AppHealthInfo.DataBean.PerformanceBean fpsBean;
        /**
         * cpu
         */
        const val PERFORMANCE_TYPE_CPU = 1

        /**
         * memory
         */
        const val PERFORMANCE_TYPE_MEMORY = 2

        /**
         * fps
         */
        const val PERFORMANCE_TYPE_FPS = 3
    }
}