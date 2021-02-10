package com.didichuxing.doraemonkit.aop.map;

import com.amap.api.location.AMapLocationListener;
import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocationListener;
import com.didichuxing.doraemonkit.kit.gpsmock.GpsMockProxyManager;
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
    public static void unRegisterAmapLocationListener(AMapLocationListener locationListener) {
        GpsMockProxyManager.getInstance().removeAMapLocationListener(locationListener);
    }

    public static void unRegisterTencentLocationListener(TencentLocationListener locationListener) {
        GpsMockProxyManager.getInstance().removeTencentLocationListener(locationListener);
    }

    public static void unRegisterBDLocationListener(BDLocationListener locationListener) {
        GpsMockProxyManager.getInstance().removeBDLocationListener(locationListener);
    }

    public static void unRegisterBDLocationListener(BDAbstractLocationListener locationListener) {
        GpsMockProxyManager.getInstance().removeBDAbsLocationListener(locationListener);
    }


}
