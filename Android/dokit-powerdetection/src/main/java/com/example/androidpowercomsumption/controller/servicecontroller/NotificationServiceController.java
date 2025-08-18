package com.example.androidpowercomsumption.controller.servicecontroller;

import android.util.Log;
import com.example.androidpowercomsumption.utils.monitor.LogFileWriter;
import com.example.androidpowercomsumption.utils.systemservice.hooker.NotificationServiceHooker;

public class NotificationServiceController {
    private final String TAG = "ServiceController";

    private NotificationServiceHooker notificationServiceHooker;

    private int preCreateChannelTime = 0;

    private int preNotifyTime = 0;

    public NotificationServiceController(NotificationServiceHooker notificationServiceHooker) {
        this.notificationServiceHooker = notificationServiceHooker;
    }

    public void start() {
        notificationServiceHooker.sHookHelper.doHook();

    }

    public void finish() {
        notificationServiceHooker.sHookHelper.doUnHook();
        Log.d(TAG, "NotificationServiceController: createChannelTime: " + (notificationServiceHooker.createChannelTime - preCreateChannelTime));
        LogFileWriter.write("创建通知的次数:" + (notificationServiceHooker.createChannelTime - preCreateChannelTime));
        Log.d(TAG, "NotificationServiceController: notifyTime: " + (notificationServiceHooker.notifyTime - preNotifyTime));
        LogFileWriter.write("通知的次数:" + (notificationServiceHooker.notifyTime - preNotifyTime));
        this.preCreateChannelTime = notificationServiceHooker.createChannelTime;
        this.preNotifyTime = notificationServiceHooker.notifyTime;
    }
}
