package com.example.androidpowercomsumption.controller.servicecontroller;


import android.util.Log;
import com.example.androidpowercomsumption.utils.monitor.LogFileWriter;
import com.example.androidpowercomsumption.utils.systemservice.hooker.WifiServiceHooker;

public class WifiServiceController {
    private final String TAG = "ServiceController";

    private WifiServiceHooker wifiServiceHooker;

    private int preScanTime = 0;

    private int preGetScanResultTime = 0;

    public WifiServiceController(WifiServiceHooker wifiServiceHooker) {
        this.wifiServiceHooker = wifiServiceHooker;
    }

    public void start() {
        wifiServiceHooker.sHookHelper.doHook();
    }

    public void finish() {
        wifiServiceHooker.sHookHelper.doUnHook();
        Log.d(TAG, "WifiServiceController: scanTime: " + (wifiServiceHooker.scanTime - preScanTime));
        LogFileWriter.write("搜索wifi的次数: " + (wifiServiceHooker.scanTime - preScanTime));
        Log.d(TAG, "WifiServiceController: getScanResultTime: " + (wifiServiceHooker.getScanResultTime - preGetScanResultTime));
        LogFileWriter.write("查询搜索结果次数: " + (wifiServiceHooker.getScanResultTime - preGetScanResultTime));
        this.preScanTime = wifiServiceHooker.scanTime;
        this.preGetScanResultTime = wifiServiceHooker.getScanResultTime;
    }
}
