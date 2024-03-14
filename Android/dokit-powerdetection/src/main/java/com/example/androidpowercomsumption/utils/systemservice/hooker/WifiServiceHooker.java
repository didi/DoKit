package com.example.androidpowercomsumption.utils.systemservice.hooker;

import android.content.Context;
import android.util.Log;
import androidx.annotation.Nullable;

import java.lang.reflect.Method;

public class WifiServiceHooker {
    private static final String TAG = "ServiceController";

    public int scanTime;

    public int getScanResultTime;

    public ServiceHookCallback sHookCallback;

    public SystemServiceHooker sHookHelper;

    public WifiServiceHooker() {
        scanTime = 0;
        getScanResultTime = 0;
        sHookCallback = new ServiceHookCallback() {
            @Override
            public void invoke(Method method, Object[] args) {
                if ("startScan".equals(method.getName())) {
                    scanTime++;
                    Log.d(TAG, "WifiServiceHooker: scan++ ");
                } else if ("getScanResults".equals(method.getName())) {
                    getScanResultTime++;
                    Log.d(TAG, "WifiServiceHooker: getScanResults++");
                }
            }

            @Nullable
            @Override
            public Object intercept(Object receiver, Method method, Object[] args) throws Throwable {
                return null;
            }
        };

        sHookHelper = new SystemServiceHooker(Context.WIFI_SERVICE, "android.net.wifi.IWifiManager", sHookCallback);
    }
}
