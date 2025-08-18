package com.example.androidpowercomsumption.utils;


import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import androidx.annotation.NonNull;
import com.example.androidpowercomsumption.controller.*;
import com.example.androidpowercomsumption.utils.monitor.TimeMonitor;
import com.example.androidpowercomsumption.utils.state.DeviceStateListener;
import com.example.androidpowercomsumption.utils.systemservice.SimulateSystemService;


public class AppStateApplication extends Application {
    private static final String TAG = "AppStateController";

    private final DeviceStateListener listener = new DeviceStateListener(this);

    private final DeviceStateController deviceStateController = new DeviceStateController();

    @Override
    public void onCreate() {
        super.onCreate();
        //注册自己的Activity的生命周期回调接口。
        registerActivityLifecycleCallbacks(new MyActivityLifecycleCallbacks(this.deviceStateController, this));
        /**
         * 注册监听设备状态
         */
        listener.register(new DeviceStateListener.ScreenStateListener() {

            boolean isFirst = true; // 第一次启动

            // 设备状态监控
            @Override
            public void onScreenOn() {
                Log.d(TAG + "Device", "屏幕点亮");
                if (isFirst) {
                    isFirst = false;
                    deviceStateController.start();
                    deviceStateController.status = true; // 亮屏状态
                    deviceStateController.curStatusStartTime = deviceStateController.startTime;
                } else {
                    if (!deviceStateController.status) { // 息屏进入亮屏
                        deviceStateController.status = true;
                        deviceStateController.curStatusEndTime = System.currentTimeMillis(); // 息屏状态的结束时间
                        deviceStateController.screenOffTime += (deviceStateController.curStatusEndTime - deviceStateController.curStatusStartTime);
                        deviceStateController.curStatusStartTime = System.currentTimeMillis(); // 息屏进入亮屏，亮屏状态的开始时间
                    }
                }
            }

            @Override
            public void onScreenOff() {
                Log.d(TAG + "Device", "屏幕熄灭");
                if (deviceStateController.status) { // 亮屏进入息屏
                    deviceStateController.status = false;
                    deviceStateController.curStatusEndTime = System.currentTimeMillis(); // 亮屏状态的结束时间
                    deviceStateController.screenOnTime += (deviceStateController.curStatusEndTime - deviceStateController.curStatusStartTime);
                    deviceStateController.curStatusStartTime = System.currentTimeMillis(); // 亮屏进入息屏，息屏状态的开始时间

                }
            }

            @Override
            public void onUserPresent() {

            }

            boolean isFirstCharge = true; // 第一次充电

            boolean isCharge = false;

            @Override
            public void onPowerConnected() {
                Log.d(TAG + "Device", "开始充电");
                if (isFirstCharge) { // 第一次充电
                    isFirstCharge = false;
                    isCharge = true;
                    deviceStateController.curStatusStartTimeCharge = System.currentTimeMillis();
                } else {
                    if (!isCharge) { // 从不充电变为充电状态
                        isCharge = true;
                        deviceStateController.curStatusEndTimeCharge = System.currentTimeMillis(); // 不充电状态的结束时间
                        deviceStateController.noChargeTime += (deviceStateController.curStatusEndTimeCharge - deviceStateController.curStatusStartTimeCharge);
                        deviceStateController.curStatusStartTimeCharge = System.currentTimeMillis();// 充电状态的开始时间

                    }
                }
            }

            @Override
            public void onPowerDisconnected() {
                Log.d(TAG + "Device", "停止充电");
                if (isCharge) { // 从充电状态变为不充电状态
                    isCharge = false;
                    deviceStateController.curStatusEndTimeCharge = System.currentTimeMillis(); // 充电状态的结束时间
                    deviceStateController.chargeTime += (deviceStateController.curStatusEndTimeCharge - deviceStateController.curStatusStartTimeCharge);
                    deviceStateController.curStatusStartTimeCharge = System.currentTimeMillis(); // 不充电状态的开始时间

                }

            }
        });
    }


    //声明一个监听Activity们生命周期的接口
    static class MyActivityLifecycleCallbacks implements ActivityLifecycleCallbacks {
        private Context context;

        private final TimeMonitor timeMonitor;


        public MyActivityLifecycleCallbacks(DeviceStateController deviceStateController, Context context) {
//            this.deviceStateController = deviceStateController;
            this.context = context;
            this.timeMonitor = new TimeMonitor(deviceStateController);
        }

        /**
         * application下的每个Activity声明周期改变时，都会触发以下的函数。
         */
        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            timeMonitor.startMonitor();
        }

        @Override
        public void onActivityStarted(Activity activity) {
        }

        @Override
        public void onActivityResumed(Activity activity) {
            timeMonitor.toFrontend();
            // todo
            SimulateSystemService.wifi(context);
            SimulateSystemService.gps(context);
            SimulateSystemService.bluetooth(context);
            SimulateSystemService.alarm(context);
            SimulateSystemService.notify(context);
        }

        @Override
        public void onActivityPaused(Activity activity) {
            timeMonitor.toBackend();
            // todo
            SimulateSystemService.wifi(context);
            SimulateSystemService.gps(context);
            SimulateSystemService.bluetooth(context);
            SimulateSystemService.alarm(context);
            SimulateSystemService.notify(context);

            if (activity.isFinishing()) {
                timeMonitor.stopMonitor();
            }
        }

        @Override
        public void onActivityStopped(Activity activity) {
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
        }

        @Override
        public void onActivityDestroyed(@NonNull Activity activity) {
            // timeMonitor.stopMonitor();
        }
    }

}