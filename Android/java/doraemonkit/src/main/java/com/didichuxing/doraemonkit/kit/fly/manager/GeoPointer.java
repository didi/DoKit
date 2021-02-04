package com.didichuxing.doraemonkit.kit.fly.manager;

import java.text.DecimalFormat;
import java.util.Objects;

public class GeoPointer {
    static DecimalFormat sDecimalFormat = new DecimalFormat("0.000000");
    double mLongitude;
    double mLatitude;
    public GeoPointer(double latitude, double longitude) {
        this.mLatitude = latitude;
        this.mLongitude = longitude;
    }

    public static boolean outOfChina(double lat, double lng) {
        if ((lng < 72.004) || (lng > 137.8347)) {
            return true;
        }
        if ((lat < 0.8293) || (lat > 55.8271)) {
            return true;
        }
        return false;
    }

    /**
     * @param lat 纬度
     * @param lng 经度
     * @return delta[0] 是纬度差，delta[1]是经度差
     */
    protected static double[] delta(double lat, double lng) {
        double[] delta = new double[2];
        double a = 6378137.0;
        double ee = 0.00669342162296594323;
        double dLat = transformLat(lng - 105.0, lat - 35.0);
        double dLng = transformLon(lng - 105.0, lat - 35.0);
        double radLat = lat / 180.0 * Math.PI;
        double magic = Math.sin(radLat);
        magic = 1 - ee * magic * magic;
        double sqrtMagic = Math.sqrt(magic);
        delta[0] = (dLat * 180.0) / ((a * (1 - ee)) / (magic * sqrtMagic) * Math.PI);
        delta[1] = (dLng * 180.0) / (a / sqrtMagic * Math.cos(radLat) * Math.PI);
        return delta;
    }

    private static double transformLat(double x, double y) {
        double ret =
                -100.0 + 2.0 * x + 3.0 * y + 0.2 * y * y + 0.1 * x * y + 0.2 * Math.sqrt(Math.abs(x));
        ret += (20.0 * Math.sin(6.0 * x * Math.PI) + 20.0 * Math.sin(2.0 * x * Math.PI)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(y * Math.PI) + 40.0 * Math.sin(y / 3.0 * Math.PI)) * 2.0 / 3.0;
        ret += (160.0 * Math.sin(y / 12.0 * Math.PI) + 320 * Math.sin(y * Math.PI / 30.0)) * 2.0 / 3.0;
        return ret;
    }

    private static double transformLon(double x, double y) {
        double ret = 300.0 + x + 2.0 * y + 0.1 * x * x + 0.1 * x * y + 0.1 * Math.sqrt(Math.abs(x));
        ret += (20.0 * Math.sin(6.0 * x * Math.PI) + 20.0 * Math.sin(2.0 * x * Math.PI)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(x * Math.PI) + 40.0 * Math.sin(x / 3.0 * Math.PI)) * 2.0 / 3.0;
        ret +=
                (150.0 * Math.sin(x / 12.0 * Math.PI) + 300.0 * Math.sin(x / 30.0 * Math.PI)) * 2.0 / 3.0;
        return ret;
    }

    public double getLongitude() {
        return mLongitude;
    }

    public void setLongitude(double longitude) {
        this.mLongitude = longitude;
    }

    public double getLatitude() {
        return mLatitude;
    }

    public void setLatitude(double latitude) {
        this.mLatitude = latitude;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        } else {
            if (other instanceof GeoPointer) {
                GeoPointer otherPointer = (GeoPointer) other;
                return sDecimalFormat.format(mLatitude).equals(sDecimalFormat.format(otherPointer.mLatitude))
                        && sDecimalFormat.format(mLongitude).equals(sDecimalFormat.format(otherPointer.mLongitude));
            } else {
                return false;
            }
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(mLongitude, mLatitude);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("latitude, longitude: ");
        sb.append(mLatitude).append(" , ").append(mLongitude);
        return sb.toString();
    }

    public double distance(GeoPointer target) {
        double earthR = 6371000;
        double x =
                Math.cos(this.mLatitude * Math.PI / 180) * Math.cos(target.mLatitude * Math.PI / 180)
                        * Math.cos((this.mLongitude - target.mLongitude) * Math.PI / 180);
        double y = Math.sin(this.mLatitude * Math.PI / 180) * Math.sin(target.mLatitude * Math.PI / 180);
        double s = x + y;
        if (s > 1) {
            s = 1;
        }
        if (s < -1) {
            s = -1;
        }
        double alpha = Math.acos(s);
        double distance = alpha * earthR;
        return distance;
    }
}