package com.didichuxing.doraemonkit.aop;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.didichuxing.doraemonkit.kit.gpsmock.GpsMockManager;

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2019-12-15-16:18
 * 描    述：高德AMapLocationListenerProxy 通过ASM代码动态插入
 * 修订历史：
 * ================================================
 */
public class AMapLocationListenerProxy implements AMapLocationListener {
    AMapLocationListener aMapLocationListener;

    public AMapLocationListenerProxy(AMapLocationListener aMapLocationListener) {
        this.aMapLocationListener = aMapLocationListener;
    }

    @Override
    public void onLocationChanged(AMapLocation mapLocation) {
        if (GpsMockManager.getInstance().isMocking()) {
            try {
                mapLocation.setLatitude(GpsMockManager.getInstance().getLatitude());
                mapLocation.setLongitude(GpsMockManager.getInstance().getLongitude());
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        if (aMapLocationListener != null) {
            aMapLocationListener.onLocationChanged(mapLocation);
        }
    }
}
