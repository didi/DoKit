package com.didichuxing.doraemonkit.gps_mock.lbs.preset;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.didichuxing.doraemonkit.gps_mock.lbs.common.Constants;
import com.didichuxing.doraemonkit.gps_mock.lbs.common.LocInfo;

public class FloatGpsPresetMockCache {

    public static LocInfo sMockLocInfo = null;

    public FloatGpsPresetMockCache() {
        if (!sMockLocationList.contains(sCustomLocInfo)) {
            sMockLocationList.add(0, sCustomLocInfo);
        }
    }

    public static MockLocList sMockLocationList = new MockLocList();
    static LocInfo sCustomLocInfo = new LocInfo("自定义点", 40.06, 116.233);

    public static void addMockLocConfig(@NonNull LocInfo... locInfos) {
        for (LocInfo locInfo : locInfos) {
            if (!sMockLocationList.contains(locInfo)) {
                sMockLocationList.add(locInfo);
            }
        }
    }

    public static void updateCustomMockLocation(double latitude, double longitude) {
        sCustomLocInfo.lat = latitude;
        sCustomLocInfo.lng = longitude;

        if (!sMockLocationList.contains(sCustomLocInfo)) {
            sMockLocationList.add(0, sCustomLocInfo);
        }

        setMockLocConfig(sCustomLocInfo);
    }

    public static void addMockLocationConfigWithJson(String jsonStr) {
        MockLocList locationList = Constants.GSON.fromJson(jsonStr, MockLocList.class);
        if (locationList == null) {
            return;
        }
        for (LocInfo locInfo : locationList) {
            addMockLocConfig(locInfo);
        }
    }

    @Nullable
    public static LocInfo getMockLocConfig() {
        if (sMockLocInfo != null) {
            return sMockLocInfo;
        }
        String configLocalJson = Constants.loadLocationConfigJson();
        if (!TextUtils.isEmpty(configLocalJson)) {
            sMockLocInfo = LocInfo.fromGson(configLocalJson);
        }
        return sMockLocInfo;
    }

    public static void setMockLocConfig(LocInfo locInfo) {
        sMockLocInfo = locInfo;
        Constants.saveLocationConfigJson(locInfo.toGson());
    }
}
