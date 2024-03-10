package com.didichuxing.doraemonkit.gps_mock.map;

import android.location.LocationManager;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.didichuxing.doraemonkit.config.GpsMockConfig;
import com.didichuxing.doraemonkit.gps_mock.gpsmock.GpsMockManager;
import com.didichuxing.doraemonkit.gps_mock.gpsmock.GpsMockProxyManager;
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
public class AMapLocationListenerProxy implements AMapLocationListener {
    private static final String TAG = AMapLocationListenerProxy.class.getSimpleName();
    public AMapLocationListener aMapLocationListener;

    public AMapLocationListenerProxy(AMapLocationListener aMapLocationListener) {
        this.aMapLocationListener = aMapLocationListener;
        GpsMockProxyManager.INSTANCE.addAMapLocationListenerProxy(this);
    }

    @Override
    public void onLocationChanged(AMapLocation mapLocation) {
        if (GpsMockManager.getInstance().isMocking()) {
            try {
                double[] res = CoordinateUtils.bd09ToGcj02(GpsMockManager.getInstance().getLongitude(), GpsMockManager.getInstance().getLatitude());
                LogHelper.d(TAG, "===amap===origin_loc==>" + mapLocation.toString()
                    + "\n before_trans_loc==>" + GpsMockManager.getInstance().getLatitude() + "   lng==>" +GpsMockManager.getInstance().getLongitude()
                    + "\n after_trans_loc==>" + Arrays.toString(res));
                mapLocation.setLatitude(res[1]);
                mapLocation.setLongitude(res[0]);
                if (GpsMockManager.getInstance().isMockingRoute()) {
                    mapLocation.setSpeed(GpsMockConfig.getRouteMockSpeed());
                }
                mapLocation.setProvider(LocationManager.GPS_PROVIDER);
                //通过反射强制改变p的值 原因:看mapLocation.setErrorCode
                ReflectUtils.reflect(mapLocation).field("p", 0);
                mapLocation.setErrorInfo("success");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (aMapLocationListener != null) {
            aMapLocationListener.onLocationChanged(mapLocation);
        }
    }
}
