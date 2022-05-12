package com.didichuxing.doraemonkit.gps_mock.map;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.didichuxing.doraemonkit.gps_mock.gpsmock.GpsMockManager;
import com.didichuxing.doraemonkit.gps_mock.gpsmock.LocationBuilder;
import com.didichuxing.doraemonkit.util.ReflectUtils;

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2019-12-15-16:18
 * 描    述：高德AMapLocationListenerProxy 通过ASM代码动态插入 高德不会跟随系统hook 腾讯和百度会跟随系统的hook
 * 修订历史：
 * ================================================
 */
public class AMapLocationClientProxy {
    private static final String TAG = "AMapLocationClientProxy";

    public static AMapLocation getLastKnownLocation(AMapLocationClient client) {
        try {
            if (client == null) {
                return null;
            }

            if (GpsMockManager.getInstance().isMocking()) {
                AMapLocation mapLocation = LocationBuilder.toAMapLocation(new LocationBuilder().build());
                mapLocation.setLatitude(GpsMockManager.getInstance().getLatitude());
                mapLocation.setLongitude(GpsMockManager.getInstance().getLongitude());
                //通过反射强制改变p的值 原因:看mapLocation.setErrorCode
                ReflectUtils.reflect(mapLocation).field("p", 0);
                mapLocation.setErrorInfo("success");
                return mapLocation;
            } else {
                //AMapLocationClient sdk 源码
                com.loc.d b = ReflectUtils.reflect(client).field("b").get();
                if (b != null) {
                    return b.e();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }
}
