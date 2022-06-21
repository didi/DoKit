package com.didichuxing.doraemonkit.gps_mock.map;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.didichuxing.doraemonkit.gps_mock.gpsmock.GpsMockManager;
import com.didichuxing.doraemonkit.gps_mock.gpsmock.GpsMockProxyManager;
import com.didichuxing.doraemonkit.util.LogHelper;

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2019-12-15-16:18
 * 描    述：百度BDLocationListenerProxy 通过ASM代码动态插入 高德不会跟随系统hook 腾讯和百度会跟随系统的hook
 * 修订历史：
 * ================================================
 */
public class BDLocationListenerProxy implements BDLocationListener {
    public BDLocationListener mBdLocationListener;

    public BDLocationListenerProxy(BDLocationListener bdLocationListener) {
        this.mBdLocationListener = bdLocationListener;
        GpsMockProxyManager.INSTANCE.addBDLocationListenerProxy(this);
    }


    @Override
    public void onReceiveLocation(BDLocation bdLocation) {
        LogHelper.d("BDLocationListenerProxy", bdLocation.toString() + " mock lat" + GpsMockManager.getInstance().getLatitude() + " " + " mock lon" + GpsMockManager.getInstance().getLongitude());
        if (GpsMockManager.getInstance().isMocking()) {
            try {
                bdLocation.setLatitude(GpsMockManager.getInstance().getLatitude());
                bdLocation.setLongitude(GpsMockManager.getInstance().getLongitude());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (mBdLocationListener != null) {
            mBdLocationListener.onReceiveLocation(bdLocation);
        }
    }


}
