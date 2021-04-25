package com.didichuxing.doraemonkit.kit.gpsmock;

import android.content.Context;
import android.location.GnssStatus;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;

import androidx.annotation.RequiresApi;

import com.didichuxing.doraemonkit.util.ReflectUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
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
//        methodHandlers.put("getLastKnownLocation", new GetLastKnownLocationMethodHandler());
        methodHandlers.put("registerGnssStatusCallback", new registerGnssStatusCallbackMethodHandler());
        // methodHandlers.put("getGpsStatus", new getGpsStatusMethodHandler());
        return methodHandlers;
    }

    @Override
    public void replaceBinder(Context context, IBinder proxy) throws NoSuchFieldException, IllegalAccessException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException {
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

    static class GetLastKnownLocationMethodHandler implements MethodHandler {
        /**
         * @param originObject 原始对象 即 LocationManagerService
         * @param proxyObject  生成的代理对象
         * @param method       需要被代理的方法
         * @param args         代理方法的参数
         * @return
         * @throws InvocationTargetException
         * @throws IllegalAccessException
         * @throws NoSuchMethodException
         */
        @Override
        public Object onInvoke(Object originObject, Object proxyObject, Method method, Object[] args) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
            if (!GpsMockManager.getInstance().isMocking()) {
                return method.invoke(originObject, args);
            }
            Location lastKnownLocation = (Location) method.invoke(originObject, args);
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


    static class GetLastLocationMethodHandler implements MethodHandler {

        @Override
        public Object onInvoke(Object originObject, Object proxyObject, Method method, Object[] args) throws InvocationTargetException, IllegalAccessException {
            if (!GpsMockManager.getInstance().isMocking()) {
                return method.invoke(originObject, args);
            }
            Location lastLocation = (Location) method.invoke(originObject, args);
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
     * 注册全球定位系统的
     */
    static class registerGnssStatusCallbackMethodHandler implements MethodHandler {

        @Override
        public Object onInvoke(Object originObject, Object proxyObject, Method method, Object[] args) throws InvocationTargetException, IllegalAccessException {
            Log.i(TAG, "registerGnssStatusCallbackMethodHandler====>registerGnssStatus  " + originObject.toString() + "  proxyObject===>" + proxyObject.toString());
            if (!GpsMockManager.getInstance().isMocking()) {
                return method.invoke(originObject, args);
            }
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                Object gnssStatusListenerTransport = args[0];
                GnssStatus.Callback callback = ReflectUtils.reflect(gnssStatusListenerTransport).field("mGnssCallback").get();
                GnssStatusCallbackProxy callbackProxy = new GnssStatusCallbackProxy(callback);
                ReflectUtils.reflect(gnssStatusListenerTransport).field("mGnssCallback", callbackProxy);
            }

            return method.invoke(originObject, args);
        }
    }


    /**
     * 获取Gps 状态
     */
    static class getGpsStatusMethodHandler implements MethodHandler {

        @Override
        public Object onInvoke(Object originObject, Object proxyObject, Method method, Object[] args) throws InvocationTargetException, IllegalAccessException {
            Object gpsStatus = method.invoke(originObject, args);
            Log.i(TAG, "getGpsStatusMethodHandler===>" + gpsStatus.toString());
            if (!GpsMockManager.getInstance().isMocking()) {
                return gpsStatus;
            }
            if (gpsStatus instanceof GpsStatus) {
                mockGpsStatus((GpsStatus) gpsStatus);
            }
            return gpsStatus;
        }
    }

    public static void mockGpsStatus(GpsStatus gpsStatus) {
        try {
            Class<GpsStatus> gpsStatusCls = (Class<GpsStatus>) gpsStatus.getClass();
            Field mSatellitesField = gpsStatusCls.getField("mSatellites");
            mSatellitesField.setAccessible(true);
            SparseArray<GpsSatellite> mSatellites = new SparseArray<>();

            Class<? extends GpsSatellite> satliteClass = (Class<? extends GpsSatellite>) Class.forName("android.location.GpsSatellite");
            GpsSatellite satellite = satliteClass.newInstance();
            Field mUsedInFixField = satliteClass.getField("mUsedInFix");
            mUsedInFixField.setAccessible(true);
            mUsedInFixField.set(satellite, true);
            Field mPrnField = satliteClass.getField("mPrn");
            mPrnField.setAccessible(true);
            mPrnField.setInt(satellite, -5);

            mSatellites.append(0, satellite);
            mSatellites.append(0, satellite);
            mSatellites.append(0, satellite);
            mSatellites.append(0, satellite);
            mSatellites.append(0, satellite);

            mSatellitesField.set(gpsStatus, mSatellites);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * LocationListener代理
     */
    public static class LocationListenerProxy implements LocationListener {
        /**
         * 原始LocationListener
         */
        LocationListener locationListener;

        private LocationListenerProxy(LocationListener locationListener) {
            this.locationListener = locationListener;
            GpsMockProxyManager.INSTANCE.addLocationListenerProxy(this);
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
    static class RequestLocationUpdatesMethodHandler implements MethodHandler {
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
            try {
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
            } catch (Exception e) {
                //处理定位权限未授予的情况
                return null;
            }


        }
    }


    private static Location buildValidLocation(String provider) {
        if (TextUtils.isEmpty(provider)) {
            provider = LocationManager.GPS_PROVIDER;
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

    @RequiresApi(api = Build.VERSION_CODES.N)
    private static class GnssStatusCallbackProxy extends GnssStatus.Callback {
        GnssStatus.Callback mCallback;

        public GnssStatusCallbackProxy(GnssStatus.Callback mCallback) {
            this.mCallback = mCallback;
        }

        @Override
        public void onStarted() {
            Log.i(TAG, "GnssStatusCallbackProxy===>onStarted");
            if (mCallback != null) {
                mCallback.onStarted();
            }
        }

        @Override
        public void onStopped() {
            Log.i(TAG, "GnssStatusCallbackProxy===>onStopped");
            if (mCallback != null) {
                mCallback.onStopped();
            }
        }

        @Override
        public void onFirstFix(int ttffMillis) {
            Log.i(TAG, "GnssStatusCallbackProxy===>onFirstFix：" + ttffMillis);
            if (mCallback != null) {
                mCallback.onFirstFix(ttffMillis);
            }
        }

        @Override
        public void onSatelliteStatusChanged(GnssStatus status) {
            Log.i(TAG, "GnssStatusCallbackProxy===>onSatelliteStatusChanged：" + status);
            if (mCallback != null) {
                mCallback.onSatelliteStatusChanged(status);

            }
        }
    }

}