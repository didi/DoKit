package com.didichuxing.doraemonkit.gps_mock.gpsmock;

import android.location.Location;

import com.amap.api.location.AMapLocation;
import com.amap.api.navi.model.AMapNaviLocation;
import com.amap.api.navi.model.NaviLatLng;
import com.baidu.location.BDLocation;

public class LocationBuilder {
    private double mLatitude = -1;
    private double mLongitude = -1;
    private float mSpeed = 0.0f;
    private float mBearing = 0.0f;
    private long mTime = 0;
    private float mHorizontalAccuracyMeters = 0.0f;

    public LocationBuilder() {
    }

    public static AMapLocation toAMapLocation(Location location) {
        AMapLocation aMapLocation = new AMapLocation(location);
        aMapLocation.setSpeed(location.getSpeed());
        aMapLocation.setBearing(location.getBearing());
        aMapLocation.setAccuracy(location.getAccuracy());
        aMapLocation.setTime(location.getTime());
        return aMapLocation;
    }

    public static AMapNaviLocation toAMapNaviLocation(Location location) {
        AMapNaviLocation aMapNaviLocation = new AMapNaviLocation();
        aMapNaviLocation.setSpeed(location.getSpeed());
        aMapNaviLocation.setBearing(location.getBearing());
        aMapNaviLocation.setAccuracy(location.getAccuracy());
        aMapNaviLocation.setTime(location.getTime());
        aMapNaviLocation.setCoord(new NaviLatLng(location.getLatitude(),location.getLongitude()));
        return aMapNaviLocation;
    }



    public static TencentLocationImp toTencentLocation(Location location) {
        if (location == null) return null;
        TencentLocationImp tencentLocation = new TencentLocationImp();
        tencentLocation.setLatitude(location.getLatitude())
                .setLongitude(location.getLongitude())
                .setSpeed(location.getSpeed())
                .setBearing(location.getBearing())
                .setAccuracy(location.getAccuracy())
                .setTime(location.getTime());
        return tencentLocation;
    }

    public static BDLocation toBdLocation(Location location) {
        if (location == null) return null;
        BDLocation bdLocation = new BDLocation();
        CordTransformUtil.Point point = CordTransformUtil.wgs84tobd09(location.getLatitude(),location.getLongitude());
        bdLocation.setLatitude(point.getLatitude());
        bdLocation.setLongitude(point.getLongitude());
        bdLocation.setSpeed(location.getSpeed());
        bdLocation.setDirection(location.getBearing()); //BDLocation 对应的方向API
        bdLocation.setRadius(location.getAccuracy()); // BDLocation 对应的精度API
//            bdLocation.setTime(location.getTime());
        return bdLocation;
    }

    public LocationBuilder fromLocation(Location location) {
        mLatitude = location.getLatitude();
        mLongitude = location.getLongitude();
        mSpeed = location.getSpeed();
        mBearing = location.getBearing();
        mTime = location.getTime();
        mHorizontalAccuracyMeters = location.getAccuracy();
        return this;
    }

    public LocationBuilder setLatitude(double latitude) {
        mLatitude = latitude;
        return this;
    }

    public LocationBuilder setLongitude(double longitude) {
        mLongitude = longitude;
        return this;
    }

    public LocationBuilder setSpeed(float speed) {
        mSpeed = speed;
        return this;
    }

    public LocationBuilder setBearing(float bearing) {
        mBearing = bearing;
        return this;
    }

    public LocationBuilder setTime(long time) {
        mTime = time;
        return this;
    }

    public LocationBuilder setHorizontalAccuracyMeters(float horizontalAccuracyMeters) {
        mHorizontalAccuracyMeters = horizontalAccuracyMeters;
        return this;
    }

    public Location build() {
        Location dokit_mock_gps = new Location("DOKIT_MOCK");
        dokit_mock_gps.setLatitude(mLatitude);
        dokit_mock_gps.setLongitude(mLongitude);
        dokit_mock_gps.setSpeed(mSpeed);
        dokit_mock_gps.setBearing(mBearing);
        dokit_mock_gps.setTime(mTime);
        dokit_mock_gps.setAccuracy(mHorizontalAccuracyMeters);
        return dokit_mock_gps;

    }

}
