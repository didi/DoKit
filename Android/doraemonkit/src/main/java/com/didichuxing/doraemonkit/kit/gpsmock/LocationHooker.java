package com.didichuxing.doraemonkit.kit.gpsmock;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;

import com.didichuxing.doraemonkit.DoraemonKit;
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
//    private List<LocationListener> mListeners = new ArrayList<>();
//    private LocationListener mHookLocationListener = null;

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
        //methodHandlers.put("removeUpdates", new RemoveUpdatesMethodHandler());
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
        public Object onInvoke(Object originService, Object proxy, Method method, Object[] args) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
            LogHelper.d(TAG, "GetLastKnownLocationMethodHandler");
            if (!GpsMockManager.getInstance().isMocking()) {
                return method.invoke(originService, args);
            }
            Location lastKnownLocation = (Location) method.invoke(originService, args);
            if (lastKnownLocation == null) {
                String provider = (String) args[0].getClass().getDeclaredMethod("getProvider").invoke(args[0]);
                lastKnownLocation = buildValidLocation(provider);
            }
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
            if (lastLocation == null) {
                lastLocation = buildValidLocation(null);
            }
            lastLocation.setLongitude(GpsMockManager.getInstance().getLongitude());
            lastLocation.setLatitude(GpsMockManager.getInstance().getLatitude());
            lastLocation.setTime(System.currentTimeMillis());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                lastLocation.setElapsedRealtimeNanos(SystemClock.elapsedRealtimeNanos());
            }
            return lastLocation;
        }
    }


    /**
     * LocationListener代理
     */
    private class LocationListenerProxy implements LocationListener {
        /**
         * 原始LocationListener
         */
        LocationListener locationListener;

        private LocationListenerProxy(LocationListener locationListener) {
            this.locationListener = locationListener;
        }

        @Override
        public void onLocationChanged(Location location) {
            if (locationListener != null) {
                if (GpsMockManager.getInstance().isMocking()) {
                    location.setLongitude(GpsMockManager.getInstance().getLongitude());
                    location.setLatitude(GpsMockManager.getInstance().getLatitude());
                    location.setTime(System.currentTimeMillis());
                }
                locationListener.onLocationChanged(location);
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            if (locationListener != null) {
                locationListener.onStatusChanged(provider, status, extras);
            }
        }

        @Override
        public void onProviderEnabled(String provider) {
            if (locationListener != null) {
                locationListener.onProviderEnabled(provider);
            }
        }

        @Override
        public void onProviderDisabled(String provider) {
            if (locationListener != null) {
                locationListener.onProviderDisabled(provider);
            }
        }
    }

    /**
     * transport:ListenerTransport 内部包含LocationListener
     */
    public class RequestLocationUpdatesMethodHandler implements MethodHandler {
        /**
         * @param originService 原始对象 LocationManager#mService
         * @param proxy         生成的代理对象
         * @param method        需要被代理的方法 LocationManager#mService.requestLocationUpdates(request, transport, intent, packageName)
         * @param args          代理方法的参数 request, transport, intent, packageName
         * @return
         * @throws IllegalAccessException
         * @throws InvocationTargetException
         * @throws NoSuchFieldException
         */
        @Override
        public Object onInvoke(Object originService, Object proxy, Method method, Object[] args) throws IllegalAccessException, InvocationTargetException, NoSuchFieldException {
            LogHelper.d(TAG, "RequestLocationUpdatesMethodHandler");
            if (!GpsMockManager.getInstance().isMocking()) {
                return method.invoke(originService, args);
            }
            Object listenerTransport = args[1];
            //LocationListener mListener 类型
            Field mListenerField = listenerTransport.getClass().getDeclaredField("mListener");
            mListenerField.setAccessible(true);
            LocationListener locationListener = (LocationListener) mListenerField.get(listenerTransport);
            LocationListenerProxy locationListenerProxy = new LocationListenerProxy(locationListener);
            //将原始的LocationListener替换为LocationListenerProxy
            mListenerField.set(listenerTransport, locationListenerProxy);
            mListenerField.setAccessible(false);
            return method.invoke(originService, args);

        }
    }


    private Location buildValidLocation(String provider) {
        if (TextUtils.isEmpty(provider)) {
            provider = "gps";
        }
        Location validLocation = new Location(provider);
        validLocation.setAccuracy(5.36f);
        validLocation.setBearing(315.0f);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            validLocation.setBearingAccuracyDegrees(52.285362f);
        }
        validLocation.setSpeed(0.79f);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            validLocation.setSpeedAccuracyMetersPerSecond(0.9462558f);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            validLocation.setVerticalAccuracyMeters(8.0f);
        }
        validLocation.setTime(System.currentTimeMillis());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            validLocation.setElapsedRealtimeNanos(SystemClock.elapsedRealtimeNanos());
        }
        return validLocation;
    }
}