package com.didichuxing.doraemonkit.gps_mock.map;

import android.location.LocationListener;

import com.amap.api.location.AMapLocationListener;
import com.amap.api.navi.AMapNaviListener;
import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocationListener;
import com.didichuxing.doraemonkit.gps_mock.gpsmock.GpsMockProxyManager;
import com.tencent.map.geolocation.TencentLocationListener;

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2019-12-15-16:18
 * 描    述：高德AMapLocationListenerProxy 通过ASM代码动态插入 高德不会跟随系统hook 腾讯和百度会跟随系统的hook
 * 修订历史：
 * ================================================
 */
public class ThirdMapLocationListenerUtil {
    public static AMapLocationListenerProxy unRegisterAmapLocationListener(AMapLocationListener locationListener) {
        return GpsMockProxyManager.INSTANCE.removeAMapLocationListener(locationListener);
    }

    public static AMapNaviListenerProxy unRegisterAmapNaviListener(AMapNaviListener naviListener) {
        return GpsMockProxyManager.INSTANCE.removeAMapNaviListener(naviListener);
    }

    public static TencentLocationListenerProxy unRegisterTencentLocationListener(TencentLocationListener locationListener) {
        return GpsMockProxyManager.INSTANCE.removeTencentLocationListener(locationListener);
    }

    public static BDLocationListenerProxy unRegisterBDLocationListener(BDLocationListener locationListener) {
        return GpsMockProxyManager.INSTANCE.removeBDLocationListener(locationListener);
    }

    public static BDAbsLocationListenerProxy unRegisterBDLocationListener(BDAbstractLocationListener locationListener) {
        return GpsMockProxyManager.INSTANCE.removeBDAbsLocationListener(locationListener);
    }

    public static void unRegisterDMapLocationListener(DMapLocationListener locationListener){
        GpsMockProxyManager.INSTANCE.removeDMapLocationListener(locationListener);
    }

    public static void unRegisterLocationListener(LocationListener locationListener) {
        GpsMockProxyManager.INSTANCE.removeLocationListener(locationListener);
    }

}
