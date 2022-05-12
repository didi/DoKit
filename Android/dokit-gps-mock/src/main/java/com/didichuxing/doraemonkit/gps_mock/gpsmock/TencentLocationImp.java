package com.didichuxing.doraemonkit.gps_mock.gpsmock;

import android.os.Bundle;

import com.tencent.map.geolocation.TencentLocation;
import com.tencent.map.geolocation.TencentPoi;

import java.util.List;

class TencentLocationImp implements TencentLocation {
    public String mProvider;
    public double mLatitude;
    public double mLongitude;
    public double mAltitude;
    public float mAccuracy;
    public String mName;
    public String mAddress;
    public String mNation;
    public String mProvince;
    public String mCity;
    public String mDistrict;
    public String mTown;
    public String mVillage;
    public String mStreet;
    public String mStreetNo;
    public Integer mAreaStat;
    public List<TencentPoi> mPoiList;
    public float mBearing;
    public float mSpeed;
    public long mTime;
    public long mElapsedRealtime;
    public int mGPSRssi;
    public String mIndoorBuildingId;
    public String mIndoorBuildingFloor;
    public int mIndoorLocationType;
    public double mDirection;
    public String mCityCode;
    public String mCityPhoneCode;
    public int mCoordinateType;
    public int mIsMockGps;
    public Bundle mExtra;

    public TencentLocationImp() {
    }

    public TencentLocationImp setIsMockGps(int isMockGps) {
        this.mIsMockGps = isMockGps;
        return this;
    }

    @Override
    public String getProvider() {
        return mProvider;
    }

    public TencentLocationImp setProvider(String provider) {
        this.mProvider = provider;
        return this;
    }

    @Override
    public double getLatitude() {
        return mLatitude;
    }

    public TencentLocationImp setLatitude(double latitude) {
        this.mLatitude = latitude;
        return this;
    }

    @Override
    public double getLongitude() {
        return mLongitude;
    }

    public TencentLocationImp setLongitude(double longitude) {
        this.mLongitude = longitude;
        return this;
    }

    @Override
    public double getAltitude() {
        return mAltitude;
    }

    public TencentLocationImp setAltitude(double altitude) {
        this.mAltitude = altitude;
        return this;
    }

    @Override
    public float getAccuracy() {
        return mAccuracy;
    }

    public TencentLocationImp setAccuracy(float accuracy) {
        this.mAccuracy = accuracy;
        return this;
    }

    @Override
    public String getName() {
        return mName;
    }

    public TencentLocationImp setName(String name) {
        this.mName = name;
        return this;
    }

    @Override
    public String getAddress() {
        return mAddress;
    }

    public TencentLocationImp setAddress(String address) {
        this.mAddress = address;
        return this;
    }

    @Override
    public String getNation() {
        return mNation;
    }

    public TencentLocationImp setNation(String nation) {
        this.mNation = nation;
        return this;
    }

    @Override
    public String getProvince() {
        return mProvince;
    }

    public TencentLocationImp setProvince(String province) {
        this.mProvince = province;
        return this;
    }

    @Override
    public String getCity() {
        return mCity;
    }

    public TencentLocationImp setCity(String city) {
        this.mCity = city;
        return this;
    }

    @Override
    public String getDistrict() {
        return mDistrict;
    }

    public TencentLocationImp setDistrict(String district) {
        this.mDistrict = district;
        return this;
    }

    @Override
    public String getTown() {
        return mTown;
    }

    public TencentLocationImp setTown(String town) {
        this.mTown = town;
        return this;
    }

    @Override
    public String getVillage() {
        return mVillage;
    }

    public TencentLocationImp setVillage(String village) {
        this.mVillage = village;
        return this;
    }

    @Override
    public String getStreet() {
        return mStreet;
    }

    public TencentLocationImp setStreet(String street) {
        this.mStreet = street;
        return this;
    }

    @Override
    public String getStreetNo() {
        return mStreetNo;
    }

    public TencentLocationImp setStreetNo(String streetNo) {
        this.mStreetNo = streetNo;
        return this;
    }

    @Override
    public Integer getAreaStat() {
        return mAreaStat;
    }

    public TencentLocationImp setAreaStat(Integer areaStat) {
        this.mAreaStat = areaStat;
        return this;
    }

    @Override
    public List<TencentPoi> getPoiList() {
        return mPoiList;
    }

    public TencentLocationImp setPoiList(List<TencentPoi> poiList) {
        this.mPoiList = poiList;
        return this;
    }

    @Override
    public float getBearing() {
        return mBearing;
    }

    public TencentLocationImp setBearing(float bearing) {
        this.mBearing = bearing;
        return this;
    }

    @Override
    public float getSpeed() {
        return mSpeed;
    }

    public TencentLocationImp setSpeed(float speed) {
        this.mSpeed = speed;
        return this;
    }

    @Override
    public long getTime() {
        return mTime;
    }

    public TencentLocationImp setTime(long time) {
        this.mTime = time;
        return this;
    }

    @Override
    public long getElapsedRealtime() {
        return mElapsedRealtime;
    }

    public TencentLocationImp setElapsedRealtime(long elapsedRealtime) {
        this.mElapsedRealtime = elapsedRealtime;
        return this;
    }

    @Override
    public int getGPSRssi() {
        return mGPSRssi;
    }

    public TencentLocationImp setGPSRssi(int gPSRssi) {
        this.mGPSRssi = gPSRssi;
        return this;
    }

    @Override
    public String getIndoorBuildingId() {
        return mIndoorBuildingId;
    }

    public TencentLocationImp setIndoorBuildingId(String indoorBuildingId) {
        this.mIndoorBuildingId = indoorBuildingId;
        return this;
    }

    @Override
    public String getIndoorBuildingFloor() {
        return mIndoorBuildingFloor;
    }

    public TencentLocationImp setIndoorBuildingFloor(String indoorBuildingFloor) {
        this.mIndoorBuildingFloor = indoorBuildingFloor;
        return this;
    }

    @Override
    public int getIndoorLocationType() {
        return mIndoorLocationType;
    }

    public TencentLocationImp setIndoorLocationType(int indoorLocationType) {
        this.mIndoorLocationType = indoorLocationType;
        return this;
    }

    @Override
    public double getDirection() {
        return mDirection;
    }

    public TencentLocationImp setDirection(double direction) {
        this.mDirection = direction;
        return this;
    }

    @Override
    public String getCityCode() {
        return mCityCode;
    }

    public TencentLocationImp setCityCode(String cityCode) {
        this.mCityCode = cityCode;
        return this;
    }

    @Override
    public String getCityPhoneCode() {
        return mCityPhoneCode;
    }

    public TencentLocationImp setCityPhoneCode(String cityPhoneCode) {
        this.mCityPhoneCode = cityPhoneCode;
        return this;
    }

    @Override
    public int getCoordinateType() {
        return mCoordinateType;
    }

    public TencentLocationImp setCoordinateType(int coordinateType) {
        this.mCoordinateType = coordinateType;
        return this;
    }

    @Override
    public int isMockGps() {
        return mIsMockGps;
    }

    @Override
    public Bundle getExtra() {
        return mExtra;
    }

    public TencentLocationImp setExtra(Bundle extra) {
        this.mExtra = extra;
        return this;
    }
}
