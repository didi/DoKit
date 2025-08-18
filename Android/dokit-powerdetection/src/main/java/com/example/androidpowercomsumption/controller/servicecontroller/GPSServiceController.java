package com.example.androidpowercomsumption.controller.servicecontroller;

import android.util.Log;
import com.example.androidpowercomsumption.utils.monitor.LogFileWriter;
import com.example.androidpowercomsumption.utils.systemservice.hooker.GPSServiceHooker;

public class GPSServiceController {

    private final String TAG = "ServiceController";

    private GPSServiceHooker gpsServiceHooker;

    private int preScanTime = 0;

    public GPSServiceController(GPSServiceHooker gpsServiceHooker) {
        this.gpsServiceHooker = gpsServiceHooker;
    }

    public void start() {
        gpsServiceHooker.sHookHelper.doHook();
    }

    public void finish() {
        gpsServiceHooker.sHookHelper.doUnHook();
        Log.d(TAG, "GPSServiceController: GPS请求扫描的次数:" + (gpsServiceHooker.scanTime - preScanTime));
        LogFileWriter.write("GPS请求扫描的次数: " + (gpsServiceHooker.scanTime - preScanTime));
        this.preScanTime = gpsServiceHooker.scanTime;
    }
}
