package com.example.androidpowercomsumption.controller;

import android.util.Log;
import com.example.androidpowercomsumption.utils.monitor.LogFileWriter;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DeviceStateController {
    private static final String TAG = "AppStateController";
    public long startTime; // 监控开始时间

    public long endTime; // 监控结束时间

    public long screenOffTime; // 息屏时长

    public long screenOnTime; // 亮屏时长

    public long chargeTime; // 充电时长

    public long noChargeTime; // 未充电时长

    public long curStatusStartTimeCharge; // 当前状态的开始时间

    public long curStatusEndTimeCharge; // 当前状态的结束时间

    public double chargeRatio; // 充电时长占比

    public boolean status; // true 亮屏 false 息屏

    public long curStatusStartTime; // 当前状态的开始时间

    public long curStatusEndTime; // 当前状态的结束时间

    public double screenOffRatio; // 息屏时间占比

    public double screenOnRatio; // 亮屏时间占比

    public void start() {
        this.startTime = System.currentTimeMillis();
    }

    public void finish() {
        this.endTime = System.currentTimeMillis();
        this.screenOffRatio = screenOffTime * 1.0 / (this.screenOffTime + this.screenOnTime);
        this.screenOnRatio = screenOnTime * 1.0 / (this.screenOffTime + this.screenOnTime);
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd/HH:mm:ss");
        Date startDate = new Date(this.startTime);
        Date endDate = new Date(this.endTime);
        Log.d(TAG, "息屏时间:" + this.screenOffTime);
        LogFileWriter.write("息屏时间:" + this.screenOffTime + " ms");
        Log.d(TAG, "亮屏时间:" + this.screenOnTime);
        LogFileWriter.write("亮屏时间:" + this.screenOnTime + " ms");
        Log.d(TAG, "总运行时间:" + format.format(startDate) + "~" + format.format(endDate));
        if ((this.screenOffTime + this.screenOnTime) != 0) {
            Log.d(TAG, "息屏时间占比:" + String.valueOf(this.screenOffRatio));
            LogFileWriter.write("息屏时间占比:" + String.valueOf(this.screenOffRatio));
            Log.d(TAG, "亮屏时间占比:" + String.valueOf(this.screenOnRatio));
            LogFileWriter.write("亮屏时间占比:" + String.valueOf(this.screenOnRatio));
        }

//        this.chargeRatio = this.chargeTime * 1.0 / (this.chargeTime + this.noChargeTime);
//        Log.d(TAG + "Device", "充电时间占比:" + String.valueOf(this.chargeRatio));
         LogFileWriter.write("充电时间:" + this.chargeTime);


    }
}
