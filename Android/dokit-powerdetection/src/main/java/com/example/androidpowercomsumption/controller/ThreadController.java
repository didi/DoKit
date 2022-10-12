package com.example.androidpowercomsumption.controller;

import android.os.SystemClock;
import android.util.Log;
import com.example.androidpowercomsumption.utils.monitor.LogFileWriter;
import com.example.androidpowercomsumption.utils.state.ProcState;
import com.example.androidpowercomsumption.utils.state.ProcStateUtil;
import com.example.androidpowercomsumption.diff.ThreadConsumptionDiff;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 用来开启和关闭线程监控
 */
public class ThreadController {

    private final String TAG = "Thread";

    public long startTime = SystemClock.uptimeMillis(); // 监控开始时间

    public long endTime;


    public List<ProcState> preProcState;
    public List<ProcState> curProcState;


    // public long preCPUTime; // 监控开始时cpu运行的时间

    // public long curCPUTime; // 监控结束时cpu运行的时间


    public List<ThreadConsumptionDiff.ThreadDiff> threadDiffList;

    public void start() {
        startTime = System.currentTimeMillis();
        // 对开始时间的系统状态做快照

        // 线程
        ProcStateUtil procStateUtil = new ProcStateUtil();
        preProcState = procStateUtil.getAllThreadInfo();
        // this.preCPUTime = procStateUtil.getCPUStatus();
    }


    public void finish() {
        // 对结束时间的系统状态做快照
        this.endTime = System.currentTimeMillis();
        // this.curCPUTime = new ProcStateUtil().getCPUStatus();
        // 线程
        ProcStateUtil procStateUtil = new ProcStateUtil();
        curProcState = procStateUtil.getAllThreadInfo();
        ThreadConsumptionDiff threadConsumptionDiff = new ThreadConsumptionDiff();
        this.threadDiffList = threadConsumptionDiff.calculateDiff(this.preProcState, this.curProcState);
        SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日-HH时mm分ss秒");

        for (ThreadConsumptionDiff.ThreadDiff threadDiff : threadDiffList) {
            Date date = new Date(this.startTime);
            threadDiff.startTime = format.format(date);
            date = new Date(this.endTime);
            threadDiff.endTime = format.format(date);
        }
        for (ThreadConsumptionDiff.ThreadDiff threadDiff : threadDiffList) {
            Log.d(TAG, threadDiff.toString());
            if (threadDiff.jiffiesDiff == 0) continue;
            LogFileWriter.write("线程" + threadDiff.comm + "的jiffy消耗:" + threadDiff.jiffiesDiff + "|线程状态:" + threadDiff.state);
        }
    }
}
