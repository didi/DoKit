package com.didichuxing.doraemonkit.gps_mock.lbs.common;

import android.util.ArrayMap;

import java.util.Arrays;

public class LocInfo {
    public String locName;
    public double lat;
    public double lng;
    public ArrayMap<String, Object> extra;

    public LocInfo(String locName, double lat, double lng) {
        this.locName = locName;
        this.lat = lat;
        this.lng = lng;

    }

    public static LocInfo fromGson(String gsonStr) {
        return Constants.GSON.fromJson(gsonStr, LocInfo.class);
    }

    public ArrayMap<String, Object> getExtra() {
        return extra;
    }

    public LocInfo setExtra(ArrayMap extra) {
        this.extra = extra;
        return this;
    }

    public LocInfo putExtra(String key, Object value) {
        if (extra == null) {
            extra = new ArrayMap();
        }
        extra.put(key, value);
        return this;
    }

    public Object getExtra(String key) {
        if (extra != null && extra.containsKey(key)) {
            return extra.get(key);
        }
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LocInfo locInfo = (LocInfo) o;
        return Double.compare(locInfo.lat, lat) == 0 &&
                Double.compare(locInfo.lng, lng) == 0 &&
                isEquals(locName, locInfo.locName);
    }

    public static boolean isEquals(Object a, Object b) {
        return (a == b) || (a != null && a.equals(b));
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(new Object[]{locName, lat, lng});
    }

    @Override
    public String toString() {
        return String.format("%s\n纬:%s, 经:%s", locName, lat, lng);
    }

    public String toGson() {
        return Constants.GSON.toJson(this);
    }
}
