package com.example.androidpowercomsumption.controller.servicecontroller;

import android.util.Log;
import com.example.androidpowercomsumption.utils.monitor.LogFileWriter;
import com.example.androidpowercomsumption.utils.systemservice.hooker.BluetoothServiceHooker;

public class BluetoothServiceController {
    private final String TAG = "ServiceController";

    private BluetoothServiceHooker bluetoothServiceHooker;

    // 前一次监控时调用服务的次数
    private int preScanTime = 0;

    private int preRegisterTime = 0;

    private int preDiscoveryTime = 0;

    public BluetoothServiceController(BluetoothServiceHooker bluetoothServiceHooker) {
        this.bluetoothServiceHooker = bluetoothServiceHooker;
    }

    public void start() {
        bluetoothServiceHooker.sHookHelper.doHook();

    }

    public void finish() {
        bluetoothServiceHooker.sHookHelper.doUnHook();
        Log.d(TAG, "BluetoothServiceController: scanTime: " + (bluetoothServiceHooker.scanTime - preScanTime));
        LogFileWriter.write("搜索蓝牙的次数: " + (bluetoothServiceHooker.scanTime - preScanTime));
        Log.d(TAG, "BluetoothServiceController: registerTime: " + (bluetoothServiceHooker.registerTime - preRegisterTime));
        LogFileWriter.write("注册蓝牙的次数: " + (bluetoothServiceHooker.registerTime - preRegisterTime));
        Log.d(TAG, "BluetoothServiceController: discoveryTime: " + (bluetoothServiceHooker.discoveryTime - preDiscoveryTime));
        LogFileWriter.write("发现蓝牙的次数: " + (bluetoothServiceHooker.discoveryTime - preDiscoveryTime));
        this.preScanTime = bluetoothServiceHooker.scanTime;
        this.preDiscoveryTime = bluetoothServiceHooker.discoveryTime;
        this.preRegisterTime = bluetoothServiceHooker.registerTime;
    }
}
