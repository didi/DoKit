package com.didichuxing.doraemonkit.kit.gpsmock;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by wanglikun on 2019/4/2
 */
public class LocationHooker extends BaseServiceHooker {
    @Override
    public String getServiceName() {
        return Context.LOCATION_SERVICE;
    }

    @Override
    public String getStubName() {
        return "android.location.ILocationManager$Stub";
    }

    @Override
    public Map<String, MethodHandler> getMethodHandlers() {
        Map<String, MethodHandler> methodHandlers = new HashMap<>();
        methodHandlers.put("requestLocationUpdates", new RequestLocationUpdatesMethodHandler());
        methodHandlers.put("getLastLocation", new GetLastLocationMethodHandler());
        methodHandlers.put("getLastKnownLocation", new GetLastKnownLocationMethodHandler());
        return methodHandlers;
    }

    public class GetLastKnownLocationMethodHandler implements MethodHandler {

        @Override
        public Object onInvoke(Object originService, Object proxy, Method method, Object[] args) throws InvocationTargetException, IllegalAccessException {
            if (!GpsMockManager.getInstance().isMocking()) {
                return method.invoke(originService, args);
            }
            Location lastKnownLocation = (Location) method.invoke(originService, args);
            lastKnownLocation.setLongitude(GpsMockManager.getInstance().getLongitude());
            lastKnownLocation.setLatitude(GpsMockManager.getInstance().getLatitude());
            lastKnownLocation.setTime(System.currentTimeMillis());
            return lastKnownLocation;
        }
    }

    public class GetLastLocationMethodHandler implements MethodHandler {

        @Override
        public Object onInvoke(Object originService, Object proxy, Method method, Object[] args) throws InvocationTargetException, IllegalAccessException {
            if (!GpsMockManager.getInstance().isMocking()) {
                return method.invoke(originService, args);
            }
            Location lastLocation = (Location) method.invoke(originService, args);
            lastLocation.setLongitude(GpsMockManager.getInstance().getLongitude());
            lastLocation.setLatitude(GpsMockManager.getInstance().getLatitude());
            lastLocation.setTime(System.currentTimeMillis());
            return lastLocation;
        }
    }

    public class RequestLocationUpdatesMethodHandler implements MethodHandler {
        @Override
        public Object onInvoke(Object originService, Object proxy, Method method, Object[] args) throws IllegalAccessException, InvocationTargetException {
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
                                location.setTime(System.currentTimeMillis());
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
            return method.invoke(originService, args);
        }
    }
}