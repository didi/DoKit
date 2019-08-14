package com.didichuxing.doraemonkit.kit.gpsmock;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;

import com.didichuxing.doraemonkit.util.LogHelper;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wanglikun on 2019/4/2
 */
public class LocationHooker extends BaseServiceHooker {
    private static final String TAG = "LocationHooker";
    private List<LocationListener> mListeners = new ArrayList<>();
    private LocationListener mHookLocationListener = null;

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
        methodHandlers.put("removeUpdates", new RemoveUpdatesMethodHandler());
        methodHandlers.put("requestLocationUpdates", new RequestLocationUpdatesMethodHandler());
        methodHandlers.put("getLastLocation", new GetLastLocationMethodHandler());
        methodHandlers.put("getLastKnownLocation", new GetLastKnownLocationMethodHandler());
        return methodHandlers;
    }

    @Override
    public void replaceBinder(Context context, IBinder proxy) throws NoSuchFieldException, IllegalAccessException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException {
        LogHelper.d(TAG, "replaceBinder");
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (locationManager == null) {
            return;
        }
        Class<?> locationManagerClass = locationManager.getClass();
        Field mServiceField = locationManagerClass.getDeclaredField("mService");
        mServiceField.setAccessible(true);

        Class stub = Class.forName(getStubName());
        Method asInterface = stub.getDeclaredMethod(METHOD_ASINTERFACE, IBinder.class);
        mServiceField.set(locationManager, asInterface.invoke(null, proxy));
        mServiceField.setAccessible(false);
    }

    public class GetLastKnownLocationMethodHandler implements MethodHandler {

        @Override
        public Object onInvoke(Object originService, Object proxy, Method method, Object[] args) throws InvocationTargetException, IllegalAccessException {
            LogHelper.d(TAG, "GetLastKnownLocationMethodHandler");
            if (!GpsMockManager.getInstance().isMocking()) {
                return method.invoke(originService, args);
            }
            Location lastKnownLocation = (Location) method.invoke(originService, args);
            lastKnownLocation.setLongitude(GpsMockManager.getInstance().getLongitude());
            lastKnownLocation.setLatitude(GpsMockManager.getInstance().getLatitude());
            lastKnownLocation.setTime(System.currentTimeMillis());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                lastKnownLocation.setElapsedRealtimeNanos(SystemClock.elapsedRealtimeNanos());
            }
            return lastKnownLocation;
        }
    }

    public class GetLastLocationMethodHandler implements MethodHandler {

        @Override
        public Object onInvoke(Object originService, Object proxy, Method method, Object[] args) throws InvocationTargetException, IllegalAccessException {
            LogHelper.d(TAG, "GetLastLocationMethodHandler");
            if (!GpsMockManager.getInstance().isMocking()) {
                return method.invoke(originService, args);
            }
            Location lastLocation = (Location) method.invoke(originService, args);
            lastLocation.setLongitude(GpsMockManager.getInstance().getLongitude());
            lastLocation.setLatitude(GpsMockManager.getInstance().getLatitude());
            lastLocation.setTime(System.currentTimeMillis());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                lastLocation.setElapsedRealtimeNanos(SystemClock.elapsedRealtimeNanos());
            }
            return lastLocation;
        }
    }

    public class RequestLocationUpdatesMethodHandler implements MethodHandler {
        @Override
        public Object onInvoke(Object originService, Object proxy, Method method, Object[] args) throws IllegalAccessException, InvocationTargetException, NoSuchFieldException {
            LogHelper.d(TAG, "RequestLocationUpdatesMethodHandler");
            Field mListenerField = args[1].getClass().getDeclaredField("mListener");
            mListenerField.setAccessible(true);
            if (mHookLocationListener == null) {
                mHookLocationListener = new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        if (!GpsMockManager.getInstance().isMocking()) {
                            for (LocationListener listener : mListeners) {
                                listener.onLocationChanged(location);
                            }
                        } else {
                            location.setLongitude(GpsMockManager.getInstance().getLongitude());
                            location.setLatitude(GpsMockManager.getInstance().getLatitude());
                            location.setTime(System.currentTimeMillis());
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                                location.setElapsedRealtimeNanos(SystemClock.elapsedRealtimeNanos());
                            }
                            for (LocationListener listener : mListeners) {
                                listener.onLocationChanged(location);
                            }
                        }
                    }

                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {
                        for (LocationListener listener : mListeners) {
                            listener.onStatusChanged(provider, status , extras);
                        }
                    }

                    @Override
                    public void onProviderEnabled(String provider) {
                        for (LocationListener listener : mListeners) {
                            listener.onProviderEnabled(provider);
                        }
                    }

                    @Override
                    public void onProviderDisabled(String provider) {
                        for (LocationListener listener : mListeners) {
                            listener.onProviderDisabled(provider);
                        }
                    }
                };
                LogHelper.d(TAG, "RequestLocationUpdatesMethodHandler: create listener");
                mListeners.add((LocationListener) mListenerField.get(args[1]));
                mListenerField.set(args[1], mHookLocationListener);
                mListenerField.setAccessible(false);
                return method.invoke(originService, args);
            }

            mListeners.add((LocationListener) mListenerField.get(args[1]));
            mListenerField.setAccessible(false);
            return null;
        }
    }

    private class RemoveUpdatesMethodHandler implements MethodHandler {
        @Override
        public Object onInvoke(Object originService, Object proxy, Method method, Object[] args) throws InvocationTargetException, IllegalAccessException, NoSuchFieldException {
            Field mListenerField = args[0].getClass().getDeclaredField("mListener");
            mListeners.remove(mListenerField.get(args[0]));
            return null;
        }
    }
}