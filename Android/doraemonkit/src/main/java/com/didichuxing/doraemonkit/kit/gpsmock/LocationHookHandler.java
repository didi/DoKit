package com.didichuxing.doraemonkit.kit.gpsmock;

import android.annotation.SuppressLint;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.IBinder;

import com.didichuxing.doraemonkit.util.LogHelper;

import java.lang.reflect.Field;
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
        switch (method.getName()) {
            case "requestLocationUpdates":
                Field listenerField = args[1].getClass().getDeclaredField("mListener");
                listenerField.setAccessible(true);
                final LocationListener originalLocationListener = (LocationListener) listenerField.get(args[1]);
                LocationListener newLocationListener = new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        if (!GpsHookManager.getInstance().isMocking()) {
                            return;
                        }
                        Location mockLocation = GpsHookManager.getInstance().getLocation();
                        mockLocation.setTime(System.currentTimeMillis());
                        originalLocationListener.onLocationChanged(mockLocation);
                    }

                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {

                    }

                    @Override
                    public void onProviderEnabled(String provider) {

                    }

                    @Override
                    public void onProviderDisabled(String provider) {

                    }
                };
                listenerField.set(args[1], newLocationListener);
                listenerField.setAccessible(false);
                break;
            case "getLastLocation":
                if (!GpsHookManager.getInstance().isMocking()) {
                    break;
                }
                Location lastLocation = GpsHookManager.getInstance().getLocation();
                lastLocation.setTime(System.currentTimeMillis());
                return lastLocation;
            case "getLastKnownLocation":
                if (!GpsHookManager.getInstance().isMocking()) {
                    break;
                }
                Location lastKnownLocation = GpsHookManager.getInstance().getLocation();
                lastKnownLocation.setTime(System.currentTimeMillis());
                return lastKnownLocation;
            default:
                break;
        }
        return method.invoke(this.mOriginService, args);
    }
}