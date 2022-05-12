package com.didichuxing.doraemonkit.gps_mock.gpsmock;

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
            e.printStackTrace();
            LogHelper.e(TAG, e.toString());
        }
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        switch (method.getName()) {
            case "requestLocationUpdates":
                Field[] fields = args[1].getClass().getDeclaredFields();
                for (Field field : fields) {
                    if (field.getType() == LocationListener.class) {
                        field.setAccessible(true);
                        final LocationListener originalLocationListener = (LocationListener) field.get(args[1]);
                        LocationListener newLocationListener = new LocationListener() {
                            @Override
                            public void onLocationChanged(Location location) {
                                if (!GpsMockManager.getInstance().isMocking()) {
                                    originalLocationListener.onLocationChanged(location);
                                } else {
                                    location.setLongitude(GpsMockManager.getInstance().getLongitude());
                                    location.setLatitude(GpsMockManager.getInstance().getLatitude());
                                    originalLocationListener.onLocationChanged(location);
                                }
                            }

                            @Override
                            public void onStatusChanged(String provider, int status, Bundle extras) {
                                originalLocationListener.onStatusChanged(provider, status, extras);
                            }

                            @Override
                            public void onProviderEnabled(String provider) {
                                originalLocationListener.onProviderEnabled(provider);
                            }

                            @Override
                            public void onProviderDisabled(String provider) {
                                originalLocationListener.onProviderDisabled(provider);
                            }
                        };
                        field.set(args[1], newLocationListener);
                        field.setAccessible(false);
                    }
                }
                break;
            case "getLastLocation":
                if (!GpsMockManager.getInstance().isMocking()) {
                    break;
                }
                Location lastLocation = (Location) method.invoke(this.mOriginService, args);
                lastLocation.setLongitude(GpsMockManager.getInstance().getLongitude());
                lastLocation.setLatitude(GpsMockManager.getInstance().getLatitude());
                return lastLocation;
            case "getLastKnownLocation":
                if (!GpsMockManager.getInstance().isMocking()) {
                    break;
                }
                Location lastKnownLocation = (Location) method.invoke(this.mOriginService, args);
                lastKnownLocation.setLongitude(GpsMockManager.getInstance().getLongitude());
                lastKnownLocation.setLatitude(GpsMockManager.getInstance().getLatitude());
                return lastKnownLocation;
            default:
                break;
        }
        return method.invoke(this.mOriginService, args);
    }
}
