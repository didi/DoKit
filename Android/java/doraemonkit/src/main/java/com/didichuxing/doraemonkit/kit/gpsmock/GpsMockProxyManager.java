package com.didichuxing.doraemonkit.kit.gpsmock;

import android.location.Location;

import com.didichuxing.doraemonkit.aop.AMapLocationListenerProxy;
import com.didichuxing.doraemonkit.aop.BDAbsLocationListenerProxy;
import com.didichuxing.doraemonkit.aop.BDLocationListenerProxy;
import com.didichuxing.doraemonkit.aop.TencentLocationListenerProxy;
import com.tencent.map.geolocation.TencentLocation;

public class GpsMockProxyManager {
    private AMapLocationListenerProxy mAMapLocationListenerProxy = null;
    private BDAbsLocationListenerProxy mBDAbsLocationListenerProxy = null;
    private BDLocationListenerProxy mBDLocationListenerProxy = null;
    private TencentLocationListenerProxy mTencentLocationListenerProxy = null;

    private GpsMockProxyManager() {
    }

    public static GpsMockProxyManager getInstance() {
        return Holder.INSTANCE;
    }

    public void setAMapLocationListenerProxy(AMapLocationListenerProxy mAMapLocationListenerProxy) {
        this.mAMapLocationListenerProxy = mAMapLocationListenerProxy;
    }

    public void setBDAbsLocationListenerProxy(BDAbsLocationListenerProxy mBDAbsLocationListenerProxy) {
        this.mBDAbsLocationListenerProxy = mBDAbsLocationListenerProxy;
    }

    public void setBDLocationListenerProxy(BDLocationListenerProxy mBDLocationListenerProxy) {
        this.mBDLocationListenerProxy = mBDLocationListenerProxy;
    }

    public void setTencentLocationListenerProxy(TencentLocationListenerProxy mTencentLocationListenerProxy) {
        this.mTencentLocationListenerProxy = mTencentLocationListenerProxy;
    }

    public void clearProxy() {
        mAMapLocationListenerProxy = null;
        mBDAbsLocationListenerProxy = null;
        mBDLocationListenerProxy = null;
        mTencentLocationListenerProxy = null;
    }

    public void mockLocationWithNotify(Location location) {
        if (location == null) return;

        try {
            notifAMapLocationListenerProxy(location);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            notifyBDAbsLocationListenerProxy(location);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            notifyBDLocationListenerProxy(location);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            notifyTencentLocationListenerProxy(location);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void notifAMapLocationListenerProxy(Location location) {
        if (mAMapLocationListenerProxy != null && location != null) {
            mAMapLocationListenerProxy.onLocationChanged(LocationBuilder.toAMapLocation(location));
        }
    }

    private void notifyBDAbsLocationListenerProxy(Location location) {
        if (mBDAbsLocationListenerProxy != null && location != null) {
            mBDAbsLocationListenerProxy.onReceiveLocation(LocationBuilder.toBdLocation(location));
        }
    }

    private void notifyBDLocationListenerProxy(Location location) {
        if (mBDLocationListenerProxy != null && location != null) {
            mBDLocationListenerProxy.onReceiveLocation(LocationBuilder.toBdLocation(location));
        }
    }

    private void notifyTencentLocationListenerProxy(Location location) {
        if (mTencentLocationListenerProxy != null && location != null) {
            mTencentLocationListenerProxy.onLocationChanged(LocationBuilder.toTencentLocation(location), TencentLocation.ERROR_OK, "");
        }
    }

    private static class Holder {
        private static GpsMockProxyManager INSTANCE = new GpsMockProxyManager();
    }
}