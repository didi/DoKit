package com.didichuxing.doraemonkit.kit.gpsmock;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;

import com.didichuxing.doraemonkit.util.LogHelper;
import com.didichuxing.doraemonkit.util.ExecutorUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by wanglikun on 2018/9/20.
 */

public class GpsMockManager {
    private static final String TAG = "GpsMockManager";

    private LocationManager mLocationManager;

    private List<String> mMockProviders = new CopyOnWriteArrayList<>();

    private List<LocationListener> mLocationListeners = new ArrayList<>();

    private Location mLocation;

    private boolean isMock;

    private double mLatitude = -1;
    private double mLongitude = -1;

    private LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            String provider = location.getProvider();
            String content = "lat(" + location.getLatitude() + ") lng(" + location.getLongitude() + ")";
            LogHelper.d(TAG, provider + " : " + content);
            mLocation = location;
            for (LocationListener locationListener : mLocationListeners) {
                locationListener.onLocationChanged(location);
            }
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

    public boolean isMock() {
        return isMock;
    }

    private static class Holder {
        private static GpsMockManager INSTANCE = new GpsMockManager();
    }

    public static GpsMockManager getInstance() {
        return Holder.INSTANCE;
    }


    public void init(Context context) {
        if (isMock()) {
            return;
        }
        mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            LogHelper.d(TAG, "No Location Permission");
            return;
        }
        mMockProviders.add(LocationManager.GPS_PROVIDER);
        mMockProviders.add(LocationManager.NETWORK_PROVIDER);

        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5, mLocationListener);
        mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 5, mLocationListener);
    }

    public void startMock() {
        if (isMock()) {
            return;
        }
        for (String name : mMockProviders) {
            LocationProvider provider = mLocationManager.getProvider(name);
            if (provider == null) {
                if (name.equals(LocationManager.GPS_PROVIDER)) {
                    mLocationManager.addTestProvider(name, true, true, false, false, true, true, true, Criteria.POWER_HIGH, Criteria.ACCURACY_FINE);
                } else if (name.equals(LocationManager.NETWORK_PROVIDER)) {
                    mLocationManager.addTestProvider(name, true, false, true, false, false, false, false, Criteria.POWER_LOW, Criteria.ACCURACY_FINE);
                } else {
                    mLocationManager.addTestProvider(name, false, false, false, false, true, true, true, Criteria.POWER_LOW, Criteria.ACCURACY_FINE);
                }
            } else {
                mLocationManager.addTestProvider(name,
                        provider.requiresNetwork(),
                        provider.requiresSatellite(),
                        provider.requiresCell(),
                        provider.hasMonetaryCost(),
                        provider.supportsAltitude(),
                        provider.supportsSpeed(),
                        provider.supportsBearing(),
                        provider.getPowerRequirement(),
                        provider.getAccuracy());
            }
            mLocationManager.setTestProviderEnabled(name, true);
            mLocationManager.setTestProviderStatus(name, LocationProvider.AVAILABLE, null, System.currentTimeMillis());
        }
        isMock = true;
        ExecutorUtil.execute(new Runnable() {
            @Override
            public void run() {
                while (isMock) {
                    try {
                        Thread.sleep(100);
                        if (mLatitude == -1 || mLongitude == -1) {
                            continue;
                        }
                        for (String name : mMockProviders) {
                            Location mockLocation = new Location(name);
                            mockLocation.setLatitude(mLatitude);
                            mockLocation.setLongitude(mLongitude);
                            mockLocation.setAccuracy(0.1f);
                            mockLocation.setTime(System.currentTimeMillis());
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                                mockLocation.setElapsedRealtimeNanos(SystemClock.elapsedRealtimeNanos());
                            }
                            mLocationManager.setTestProviderLocation(name, mockLocation);
                        }
                    } catch (IllegalArgumentException e) {
                        LogHelper.d(TAG, e.toString());
                    } catch (InterruptedException e) {
                        LogHelper.d(TAG, e.toString());
                    }
                }
            }
        });
    }

    public void stopMock() {
        if (!isMock) {
            return;
        }
        try {
            for (String name : mMockProviders) {
                mLocationManager.removeTestProvider(name);
            }
        } catch (IllegalArgumentException e) {
            LogHelper.d(TAG, e.toString());
        }
    }

    public void destroy() {
        stopMock();
        isMock = false;
        mLocationManager.removeUpdates(mLocationListener);
        mMockProviders.clear();
        mLocationListeners.clear();
        mLocation = null;
    }

    public void addLocationListener(LocationListener listener) {
        mLocationListeners.add(listener);
    }

    public void removeLocationListener(LocationListener listener) {
        mLocationListeners.remove(listener);
    }

    public Location getLocation() {
        return mLocation;
    }

    public void mockLocation(double latitude, double longitude) {
        mLatitude = latitude;
        mLongitude = longitude;
    }
}