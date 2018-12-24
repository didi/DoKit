package com.didichuxing.doraemonkit.kit.gpsmock;

import android.annotation.SuppressLint;
import android.location.Location;
import android.os.IBinder;

import com.didichuxing.doraemonkit.util.LogHelper;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Created by wanglikun on 2018/12/18.
 */

public class LocationHookHandler implements InvocationHandler {
    private static final String TAG = "LocationHookHandler";

    private Object mOriginService;

    @SuppressWarnings("unchecked")
    @SuppressLint("PrivateApi")
    public LocationHookHandler(IBinder binder) {
        try {
             Class iLocationManager$Stub = Class.forName("android.location.ILocationManager$Stub");
            Method asInterface = iLocationManager$Stub.getDeclaredMethod("asInterface", IBinder.class);
            this.mOriginService = asInterface.invoke(null, binder);
        } catch (Exception e) {
            LogHelper.e(TAG, e.toString());
        }
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (GpsHookManager.getInstance().isMocking()) {
            switch (method.getName()) {
                case "getLastLocation":
                    Location lastLocation = GpsHookManager.getInstance().getLocation();
                    lastLocation.setTime(System.currentTimeMillis());
                    return lastLocation;
                case "getLastKnownLocation":
                    Location lastKnownLocation = GpsHookManager.getInstance().getLocation();
                    lastKnownLocation.setTime(System.currentTimeMillis());
                    return lastKnownLocation;
                default:
                    break;
            }
        }
        return method.invoke(this.mOriginService, args);
    }
}