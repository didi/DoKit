package com.didichuxing.doraemonkit.gps_mock.map;

import android.util.Log;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.didichuxing.doraemonkit.gps_mock.gpsmock.GpsMockManager;
import com.didichuxing.doraemonkit.gps_mock.gpsmock.GpsMockProxyManager;

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2019-12-15-16:18
 * 描    述：百度BDLocationListenerProxy 通过ASM代码动态插入 高德不会跟随系统hook 腾讯和百度会跟随系统的hook
 * 修订历史：
 * ================================================
 */
public class BDAbsLocationListenerProxy extends BDAbstractLocationListener {
    public BDAbstractLocationListener mBdLocationListener;

    public BDAbsLocationListenerProxy(BDAbstractLocationListener bdLocationListener) {
        this.mBdLocationListener = bdLocationListener;
        GpsMockProxyManager.INSTANCE.addBDAbsLocationListenerProxy(this);
    }


    @Override
    public void onReceiveLocation(BDLocation bdLocation) {
        Log.d("onReceiveLocation", "BDAbsLocationListenerProxy: " + bdLocation.toString() + " mock lat" + GpsMockManager.getInstance().getLatitude() + " " + " mock lon" + GpsMockManager.getInstance().getLongitude());
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

    @Override
    public void onConnectHotSpotMessage(String s, int i) {
        if (mBdLocationListener != null) {
            mBdLocationListener.onConnectHotSpotMessage(s, i);
        }

    }

    @Override
    public void onLocDiagnosticMessage(int var1, int var2, String var3) {
        if (mBdLocationListener != null) {
            mBdLocationListener.onLocDiagnosticMessage(var1, var2, var3);
        }


    }

    @Override
    public void onReceiveVdrLocation(BDLocation var1) {
        if (mBdLocationListener != null) {
            mBdLocationListener.onReceiveVdrLocation(var1);
        }


    }

    @Override
    public void onReceiveLocString(String var1) {
        if (mBdLocationListener != null) {
            mBdLocationListener.onReceiveLocString(var1);
        }


    }
}
