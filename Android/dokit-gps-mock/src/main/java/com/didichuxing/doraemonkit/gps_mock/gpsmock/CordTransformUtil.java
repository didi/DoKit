package com.didichuxing.doraemonkit.gps_mock.gpsmock;

public class CordTransformUtil {
    static double x_PI = 3.14159265358979324 * 3000.0 / 180.0;
    static double PI = 3.1415926535897932384626;
    static double a = 6378245.0;
    static double ee = 0.00669342162296594323;

    /**
     * 火星坐标系 (GCJ-02) 与百度坐标系 (BD-09) 的转换    * 即谷歌、高德 转 百度    * @param lng    * @param lat    * @returns {*[]}
     */
    public static Point gcj02tobd09(double lat, double lng) {
        double z = Math.sqrt(lng * lng + lat * lat) + 0.00002 * Math.sin(lat * x_PI);
        double theta = Math.atan2(lat, lng) + 0.000003 * Math.cos(lng * x_PI);
        double bd_lng = z * Math.cos(theta) + 0.0065;
        double bd_lat = z * Math.sin(theta) + 0.006;
        return new Point(bd_lat, bd_lng);
    }

    /**
     * WGS84转GCj02    * @param lng    * @param lat    * @returns {*[]}
     */
    public static Point wgs84togcj02(double lat, double lng) {
        double dlat = transformlat(lat - 35.0, lng - 105.0);
        double dlng = transformlng(lat - 35.0, lng - 105.0);
        double radlat = lat / 180.0 * PI;
        double magic = Math.sin(radlat);
        magic = 1 - ee * magic * magic;
        double sqrtmagic = Math.sqrt(magic);
        dlat = (dlat * 180.0) / ((a * (1 - ee)) / (magic * sqrtmagic) * PI);
        dlng = (dlng * 180.0) / (a / sqrtmagic * Math.cos(radlat) * PI);
        double mglat = lat + dlat;
        double mglng = lng + dlng;
        return new Point(mglat, mglng);
    }

    /**
     * GCJ02 转换为 WGS84    * @param lng    * @param lat    * @returns {*[]}
     */
    public static Point gcj02towgs84(double lat, double lng) {
        double dlat = transformlat(lat - 35.0, lng - 105.0);
        double dlng = transformlng(lat - 35.0, lng - 105.0);
        double radlat = lat / 180.0 * PI;
        double magic = Math.sin(radlat);
        magic = 1 - ee * magic * magic;
        double sqrtmagic = Math.sqrt(magic);
        dlat = (dlat * 180.0) / ((a * (1 - ee)) / (magic * sqrtmagic) * PI);
        dlng = (dlng * 180.0) / (a / sqrtmagic * Math.cos(radlat) * PI);
        double mglat = lat + dlat;
        double mglng = lng + dlng;      // Point point=new Point(mglng, mglat);      // return point;
        return new Point(mglat, mglng);
    }

    ;

    /**
     * WGS84 转换为 BD-09    * @param lng    * @param lat    * @returns {*[]}    *
     */
    public static Point wgs84tobd09(double lat, double lng) {
        //第一次转换
        double dlat = transformlat(lat - 35.0, lng - 105.0);
        double dlng = transformlng(lat - 35.0, lng - 105.0);
        double radlat = lat / 180.0 * PI;
        double magic = Math.sin(radlat);
        magic = 1 - ee * magic * magic;
        double sqrtmagic = Math.sqrt(magic);
        dlat = (dlat * 180.0) / ((a * (1 - ee)) / (magic * sqrtmagic) * PI);
        dlng = (dlng * 180.0) / (a / sqrtmagic * Math.cos(radlat) * PI);
        double mglat = lat + dlat;
        double mglng = lng + dlng;
        //第二次转换
        double z = Math.sqrt(mglng * mglng + mglat * mglat) + 0.00002 * Math.sin(mglat * x_PI);
        double theta = Math.atan2(mglat, mglng) + 0.000003 * Math.cos(mglng * x_PI);
        double bd_lng = z * Math.cos(theta) + 0.0065;
        double bd_lat = z * Math.sin(theta) + 0.006;
        return new Point(bd_lat, bd_lng);
    }

    ;

    /**
     * 百度坐标系 (BD-09) 与 火星坐标系 (GCJ-02)的转换    * 即 百度 转 谷歌、高德    * @param bd_lon    * @param bd_lat    * @returns {*[]}
     */
    public Point bd09togcj02(double bd_lat, double bd_lon) {
        double x = bd_lon - 0.0065;
        double y = bd_lat - 0.006;
        double z = Math.sqrt(x * x + y * y) - 0.00002 * Math.sin(y * x_PI);
        double theta = Math.atan2(y, x) - 0.000003 * Math.cos(x * x_PI);
        double gg_lng = z * Math.cos(theta);
        double gg_lat = z * Math.sin(theta);
        return new Point(gg_lat, gg_lng);
    }

    private static double transformlat(double lat, double lng) {
        double ret = -100.0 + 2.0 * lng + 3.0 * lat + 0.2 * lat * lat + 0.1 * lng * lat + 0.2 * Math.sqrt(Math.abs(lng));
        ret += (20.0 * Math.sin(6.0 * lng * PI) + 20.0 * Math.sin(2.0 * lng * PI)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(lat * PI) + 40.0 * Math.sin(lat / 3.0 * PI)) * 2.0 / 3.0;
        ret += (160.0 * Math.sin(lat / 12.0 * PI) + 320 * Math.sin(lat * PI / 30.0)) * 2.0 / 3.0;
        return ret;
    }

    ;

    private static double transformlng(double lat, double lng) {
        double ret = 300.0 + lng + 2.0 * lat + 0.1 * lng * lng + 0.1 * lng * lat + 0.1 * Math.sqrt(Math.abs(lng));
        ret += (20.0 * Math.sin(6.0 * lng * PI) + 20.0 * Math.sin(2.0 * lng * PI)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(lng * PI) + 40.0 * Math.sin(lng / 3.0 * PI)) * 2.0 / 3.0;
        ret += (150.0 * Math.sin(lng / 12.0 * PI) + 300.0 * Math.sin(lng / 30.0 * PI)) * 2.0 / 3.0;
        return ret;
    }

    public static void main(String[] args) {    //两次谷歌转换为百度坐标     //第一次  WGS84转GCj02
        String lnglat = wgs84togcj02(31.841652709281103, 117.20296517261839).toString();
        double lng = Double.parseDouble(lnglat.split(",")[0]);
        double lat = Double.parseDouble(lnglat.split(",")[1]);
        System.out.println("第一次转换的结果:" + lng + "," + lat);    //  第二次 gcj02tobd09
        System.out.println("第二次转换的结果:" + gcj02tobd09(lat, lng));
//谷歌转百度一次转换
        System.out.println("谷歌转换为百度一次转换的结果:" + wgs84tobd09(31.841652709281103, 117.20296517261839));
    }

    public static class Point {
        double mLat, mLng;

        @Override
        public String toString() {
            return "纬度: " + mLat + ", 经度: " + mLng;
        }

        public Point(double lat, double lng) {
            this.mLat = lat;
            this.mLng = lng;
        }

        public double getLatitude() {
            return mLat;
        }

        public Point setLatitude(double lat) {
            this.mLat = lat;
            return this;
        }

        public double getLongitude() {
            return mLng;
        }

        public Point setLongitude(double lng) {
            this.mLng = lng;
            return this;
        }
    }
}
