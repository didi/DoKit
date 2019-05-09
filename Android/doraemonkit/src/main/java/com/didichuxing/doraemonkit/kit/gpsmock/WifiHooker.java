package com.didichuxing.doraemonkit.kit.gpsmock;

import android.content.Context;
import android.net.wifi.ScanResult;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by wanglikun on 2019/4/2
 */
public class WifiHooker extends BaseServiceHooker {
    @Override
    public String getServiceName() {
        return Context.WIFI_SERVICE;
    }

    @Override
    public String getStubName() {
        return "android.net.wifi.IWifiManager$Stub";
    }

    @Override
    public Map<String, MethodHandler> getMethodHandlers() {
        Map<String, MethodHandler> methodHandlers = new HashMap<>();
        methodHandlers.put("getScanResults", new GetScanResultsMethodHandler());
        return methodHandlers;
    }

    public class GetScanResultsMethodHandler implements MethodHandler {

        @Override
        public Object onInvoke(Object originService, Object proxy, Method method, Object[] args) throws InvocationTargetException, IllegalAccessException {
            if (!GpsMockManager.getInstance().isMocking()) {
                return method.invoke(originService, args);
            }
            return new ArrayList<ScanResult>();
        }
    }
}