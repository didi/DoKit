package com.didichuxing.doraemonkit.gps_mock.map;

import android.location.LocationManager;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.didichuxing.doraemonkit.gps_mock.gpsmock.GpsMockManager;
import com.didichuxing.doraemonkit.gps_mock.gpsmock.LocationBuilder;
import com.didichuxing.doraemonkit.util.CoordinateUtils;
import com.didichuxing.doraemonkit.util.LogHelper;
import com.didichuxing.doraemonkit.util.ReflectUtils;

import java.util.Arrays;

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
                double[] res = CoordinateUtils.bd09ToGcj02(GpsMockManager.getInstance().getLongitude(), GpsMockManager.getInstance().getLatitude());
                LogHelper.d(TAG, "===amap===origin_loc==>" + mapLocation.toString()
                    + "\n before_trans_loc==>" + GpsMockManager.getInstance().getLatitude() + "   lng==>" +GpsMockManager.getInstance().getLongitude()
                    + "\n after_trans_loc==>" + Arrays.toString(res));
                mapLocation.setLatitude(res[1]);
                mapLocation.setLongitude(res[0]);
                mapLocation.setProvider(LocationManager.GPS_PROVIDER);
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
