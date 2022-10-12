package com.example.androidpowercomsumption.utils.monitor;

import android.util.Log;
import com.example.androidpowercomsumption.controller.AppStateController;
import com.example.androidpowercomsumption.controller.DeviceStateController;
import com.example.androidpowercomsumption.controller.ThreadController;
import com.example.androidpowercomsumption.controller.servicecontroller.*;
import com.example.androidpowercomsumption.utils.systemservice.hooker.*;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 监控设备运行的时间
 */
public class TimeMonitor {

    private final String TAG = "AppStateController";
    private long startMonitorTime;

    private long stopMonitorTime;

    private final AppStateController appStateController = new AppStateController();

    private ThreadController threadController = new ThreadController();

    private final DeviceStateController deviceStateController;

    private final WifiServiceController wifiServiceController = new WifiServiceController(new WifiServiceHooker());

    private final GPSServiceController gpsServiceController = new GPSServiceController(new GPSServiceHooker());

    private final BluetoothServiceController bluetoothServiceController = new BluetoothServiceController(new BluetoothServiceHooker());

    private final AlarmServiceController alarmServiceController = new AlarmServiceController(new AlarmServiceHooker());

    private final NotificationServiceController notificationServiceController = new NotificationServiceController(new NotificationServiceHooker());

    private boolean isFirst = true; // 第一次启动并且进入前台

    public long getStartMonitorTime() {
        return startMonitorTime;
    }

    public void setStartMonitorTime(long startMonitorTime) {
        this.startMonitorTime = startMonitorTime;
    }

    public long getStopMonitorTime() {
        return stopMonitorTime;
    }

    public void setStopMonitorTime(long stopMonitorTime) {
        this.stopMonitorTime = stopMonitorTime;
    }

    public TimeMonitor(DeviceStateController deviceStateController) {
        this.deviceStateController = deviceStateController;
    }

    public long getCurrentTime() {
        return System.currentTimeMillis();
    }

    // 上一次记录日志的时间
    private long lastLogTime;

    public String getCurrentFormatTime(long time) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd/HH:mm:ss");
        Date date = new Date(time);
        return format.format(date);
    }

    public void startMonitor() {
        // create file
        String filePath = "/data/data/com.example.androidpowercomsumption/files/data.txt";
        File file = null;
        try {
            file = new File(filePath);
            if (!file.exists()) {
                file.createNewFile();
            } else {
                file.delete();
                file.createNewFile();
            }

        } catch (Exception e) {
            Log.i("error:", e + "");
        }
        // 只会在启动的时候触发一次
        appStateController.start();
        appStateController.status = true; // 前台状态
        appStateController.curStatusStartTime = appStateController.startTime; // 当前状态的开始时间

        this.startMonitorTime = getCurrentTime();
        this.lastLogTime = getCurrentTime();
    }

    // app由前台进入后台
    public void toBackend() {
        Log.d(TAG, "App进入后台");
        // 结束前台时间段的监控
        // todo 输出报告
        Log.d("ServiceController", "App前台运行时间段内调用系统服务次数");
        LogFileWriter.write("=================================================================");
        LogFileWriter.write(getCurrentFormatTime(lastLogTime) + "~" + getCurrentFormatTime(getCurrentTime()) + ":(APP处于前台运行)");
        stopServiceHooker();
        stopThreadMonitor();
        // 开启后台时间段的监控
        startServiceHooker();
        startThreadMonitor();


        if (appStateController.status) { // 由前台进入后台
            appStateController.status = false;
            appStateController.curStatusEndTime = System.currentTimeMillis(); // 前台状态的结束时间
            appStateController.foregroundTime += (appStateController.curStatusEndTime - appStateController.curStatusStartTime);
            appStateController.curStatusStartTime = System.currentTimeMillis(); // 前台进入后台，后台状态的开始时间
        }
        this.lastLogTime = getCurrentTime();
    }

    public void toFrontend() {

        Log.d(TAG, "APP进入前台");

        if (isFirst) {
            // 开始前台时间段的监控
            startServiceHooker();
            startThreadMonitor();
            isFirst = false;
        } else {
            // 结束后台时间段的监控，做一次输出
            // todo 输出报告
            Log.d("ServiceController", "App后台运行时间段内调用系统服务次数");
            LogFileWriter.write("=================================================================");
            LogFileWriter.write(getCurrentFormatTime(lastLogTime) + "~" + getCurrentFormatTime(getCurrentTime()) + ":(APP处于后台运行)");
            stopServiceHooker();
            stopThreadMonitor();
            // 开始前台时间段的监控
            startServiceHooker();
            startThreadMonitor();
        }

        if (!appStateController.status) { // 后台进入前台
            appStateController.status = true;
            appStateController.curStatusEndTime = System.currentTimeMillis();// 后台状态的结束时间
            appStateController.backgroundTime += (appStateController.curStatusEndTime - appStateController.curStatusStartTime);
            appStateController.curStatusStartTime = System.currentTimeMillis();// 后台进入前台，前台状态的开始时间

        }
        this.lastLogTime = getCurrentTime();
    }

    public void stopMonitor() {
        this.stopMonitorTime = getCurrentTime();

        LogFileWriter.write("");
        LogFileWriter.write("");
        LogFileWriter.write("=================================================================");
        LogFileWriter.write(getCurrentFormatTime(this.getStartMonitorTime()) + "~" + getCurrentFormatTime(this.getStopMonitorTime()) + ":(APP整个运行阶段)");
        appStateController.finish();
        deviceStateController.finish();
    }


    public void startServiceHooker() {
        wifiServiceController.start();
        gpsServiceController.start();
        bluetoothServiceController.start();
        alarmServiceController.start();
        notificationServiceController.start();
    }

    public void stopServiceHooker() {
        wifiServiceController.finish();
        gpsServiceController.finish();
        bluetoothServiceController.finish();
        alarmServiceController.finish();
        notificationServiceController.finish();
    }


    public void startThreadMonitor() {
        threadController = new ThreadController();
        threadController.start();
    }

    public void stopThreadMonitor() {
        threadController.finish();
    }
}
