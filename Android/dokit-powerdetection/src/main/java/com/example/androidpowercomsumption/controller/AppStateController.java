package com.example.androidpowercomsumption.controller;

import android.util.Log;
import com.example.androidpowercomsumption.utils.monitor.LogFileWriter;

import java.text.SimpleDateFormat;
import java.util.Date;

public class AppStateController {

    private static final String TAG = "AppStateController";
    public long startTime; // 监控开始时间

    public long endTime; // 监控结束时间

    public long foregroundTime; // 前台运行时长

    public long backgroundTime; // 后台运行时长

    public boolean status; // true 前台 false 后台
    public long curStatusStartTime; // 当前状态的开始时间

    public long curStatusEndTime; // 当前状态的结束时间

    public double foregroundRatio; //前台运行时间占比

    public double backgroundRatio; // 后台运行时间占比


    public void start() {

        this.startTime = System.currentTimeMillis();
    }

    public void finish() {
        this.endTime = System.currentTimeMillis();
        this.foregroundRatio = foregroundTime * 1.0 / (this.foregroundTime + this.backgroundTime);
        this.backgroundRatio = backgroundTime * 1.0 / (this.foregroundTime + this.backgroundTime);
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd/HH:mm:ss");
        Date startDate = new Date(this.startTime);
        Date endDate = new Date(this.endTime);
        Log.d(TAG, "前台运行时间:" + this.foregroundTime);
        LogFileWriter.write("前台运行时间:" + this.foregroundTime + " ms");

        Log.d(TAG, "后台运行时间:" + this.backgroundTime);
        LogFileWriter.write("后台运行时间:" + this.backgroundTime + " ms");

        Log.d(TAG, "总运行时间:" + format.format(startDate) + "~" + format.format(endDate));

        Log.d(TAG, "前台运行时间占比:" + this.foregroundRatio);
        LogFileWriter.write("前台运行时间占比:" + this.foregroundRatio);

        Log.d(TAG, "后台运行时间占比:" + this.backgroundRatio);
        LogFileWriter.write("后台运行时间占比:" + this.backgroundRatio);


    }
}
