package com.didichuxing.doraemonkit.gps_mock.gpsmock;

import android.content.Context;
import android.location.GnssStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.didichuxing.doraemonkit.util.ReflectUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by jintai on 2019/4/2
 * http://weishu.me/2016/02/16/understand-plugin-framework-binder-hook/
 */
public class LocationHooker extends BaseServiceHooker {
    private static final String TAG = "LocationHooker";
//    private List<LocationListener> mListeners = new ArrayList<>();
//    private LocationListener mHookLocationListener = null;

    @Override
    public String serviceName() {
        return Context.LOCATION_SERVICE;
    }

    /**
     * 编译期动态生成
     *
     * @return
     */
    @Override
    public String stubName() {
        return "android.location.ILocationManager$Stub";
    }

    @NonNull
    @Override
    public Map<String, MethodHandler> registerMethodHandlers() {
        Map<String, MethodHandler> methodHandlers = new HashMap<>();
        //methodHandlers.put("removeUpdates", new RemoveUpdatesMethodHandler());
        methodHandlers.put("requestLocationUpdates", new RequestLocationUpdatesMethodHandler());
        methodHandlers.put("getLastLocation", new GetLastLocationMethodHandler());
//        methodHandlers.put("getLastKnownLocation", new GetLastKnownLocationMethodHandler());
        methodHandlers.put("registerGnssStatusCallback", new RegisterGnssStatusCallbackMethodHandler());
        // methodHandlers.put("getGpsStatus", new getGpsStatusMethodHandler());
        return methodHandlers;
    }

    @Override
    public void replaceBinderProxy(Context context, IBinder proxy) throws NoSuchFieldException, IllegalAccessException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException {
        //在 frameworks/base/core/java/android/app/SystemServiceRegistry.java中初始化
        //替换具体服务中的mService
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        //IInterface customService = ReflectUtils.reflect(stubName()).method("asInterface", proxy).get();
        if (getMBinderStubProxy() != null) {
            ReflectUtils.reflect(locationManager).field("mService", getMBinderStubProxy());
        }


    }

    static class GetLastKnownLocationMethodHandler extends MethodHandler {
        /**
         * @param originObject 原始对象 即 LocationManagerService
         * @param method       需要被代理的方法
         * @param args         代理方法的参数
         * @return
         * @throws InvocationTargetException
         * @throws IllegalAccessException
         * @throws NoSuchMethodException
         */
        @Override
        public Object onInvoke(Object originObject, Method method, Object[] args) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
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


    static class GetLastLocationMethodHandler extends MethodHandler {

        @Override
        public Object onInvoke(Object originObject, Method method, Object[] args) throws InvocationTargetException, IllegalAccessException {
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
    static class RegisterGnssStatusCallbackMethodHandler extends MethodHandler {

        @Override
        public Object onInvoke(Object originObject, Method method, Object[] args) throws InvocationTargetException, IllegalAccessException {
            //Log.i(TAG, "registerGnssStatusCallbackMethodHandler====>registerGnssStatus  " + originObject.toString() + "  proxyObject===>" + proxyObject.toString());
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
            Log.d(TAG, "系统定位===onLocationChanged isMock=" + GpsMockManager.getInstance().isMocking() + " " + location.getLongitude() + " "+ location.getLatitude());

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
    static class RequestLocationUpdatesMethodHandler extends MethodHandler {
        /**
         * @param originService 原始对象 LocationManager#mService
         * @param method        需要被代理的方法 LocationManager#mService.requestLocationUpdates(request, transport, intent, packageName)
         * @param args          代理方法的参数 request, transport, intent, packageName
         * @return
         * @throws IllegalAccessException
         * @throws InvocationTargetException
         * @throws NoSuchFieldException
         */
        @Override
        public Object onInvoke(Object originService, Method method, Object[] args) throws IllegalAccessException, InvocationTargetException, NoSuchFieldException {
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
            //Log.i(TAG, "GnssStatusCallbackProxy===>onStarted");
            if (mCallback != null) {
                mCallback.onStarted();
            }
        }

        @Override
        public void onStopped() {
            //Log.i(TAG, "GnssStatusCallbackProxy===>onStopped");
            if (mCallback != null) {
                mCallback.onStopped();
            }
        }

        @Override
        public void onFirstFix(int ttffMillis) {
            // Log.i(TAG, "GnssStatusCallbackProxy===>onFirstFix：" + ttffMillis);
            if (mCallback != null) {
                mCallback.onFirstFix(ttffMillis);
            }
        }

        @Override
        public void onSatelliteStatusChanged(GnssStatus status) {
            // Log.i(TAG, "GnssStatusCallbackProxy===>onSatelliteStatusChanged：" + status);
            if (mCallback != null) {
                mCallback.onSatelliteStatusChanged(status);

            }
        }
    }

}
