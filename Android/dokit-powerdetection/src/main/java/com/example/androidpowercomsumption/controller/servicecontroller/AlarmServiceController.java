package com.example.androidpowercomsumption.controller.servicecontroller;

import android.util.Log;
import com.example.androidpowercomsumption.utils.monitor.LogFileWriter;
import com.example.androidpowercomsumption.utils.systemservice.hooker.AlarmServiceHooker;

public class AlarmServiceController {
    private final String TAG = "ServiceController";

    private AlarmServiceHooker alarmServiceHooker;

    private int preSetTime = 0;

    public AlarmServiceController(AlarmServiceHooker alarmServiceHooker) {
        this.alarmServiceHooker = alarmServiceHooker;
    }

    public void start() {
        alarmServiceHooker.sHookHelper.doHook();

    }

    public void finish() {
        alarmServiceHooker.sHookHelper.doUnHook();
        LogFileWriter.write("调用设置提醒服务的次数: " + (alarmServiceHooker.setTime - preSetTime));
        Log.d(TAG, "AlarmServiceController: setTime: " + (alarmServiceHooker.setTime - preSetTime));
        this.preSetTime = alarmServiceHooker.setTime;
    }
}
