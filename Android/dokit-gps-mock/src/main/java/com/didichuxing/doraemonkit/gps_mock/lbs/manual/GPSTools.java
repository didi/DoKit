package com.didichuxing.doraemonkit.gps_mock.lbs.manual;

public final class GPSTools {
    private GPSTools() {
    }

    public static double getLngDiff(double lat, double distance) {
        double r = 6378137.0D * Math.cos(rad(lat));
        double interval = distance / r;
        return angle(interval);
    }

    public static double getLatDiff(double distance) {
        return angle(distance / 6378137.0D);
    }

    private static double rad(double d) {
        return d * 3.141592653589793D / 180.0D;
    }

    private static double angle(double rad) {
        return rad * 180.0D / 3.141592653589793D;
    }
}
