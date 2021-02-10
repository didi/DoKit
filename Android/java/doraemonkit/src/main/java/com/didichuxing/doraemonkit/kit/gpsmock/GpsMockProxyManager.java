package com.didichuxing.doraemonkit.kit.gpsmock;

import android.location.Location;

import com.amap.api.location.AMapLocationListener;
import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocationListener;
import com.didichuxing.doraemonkit.aop.map.AMapLocationListenerProxy;
import com.didichuxing.doraemonkit.aop.map.BDAbsLocationListenerProxy;
import com.didichuxing.doraemonkit.aop.map.BDLocationListenerProxy;
import com.didichuxing.doraemonkit.aop.map.TencentLocationListenerProxy;
import com.tencent.map.geolocation.TencentLocation;
import com.tencent.map.geolocation.TencentLocationListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 三方地图管理类
 */
public class GpsMockProxyManager {
    private final List<AMapLocationListenerProxy> mAMapLocationListenerProxys = new ArrayList<>();
    private final List<BDAbsLocationListenerProxy> mBDAbsLocationListenerProxys = new ArrayList<>();
    private final List<BDLocationListenerProxy> mBDLocationListenerProxys = new ArrayList<>();
    private final List<TencentLocationListenerProxy> mTencentLocationListenerProxys = new ArrayList<>();

    private GpsMockProxyManager() {
    }

    public static GpsMockProxyManager getInstance() {
        return Holder.INSTANCE;
    }

    public void addAMapLocationListenerProxy(AMapLocationListenerProxy aMapLocationListenerProxy) {
        this.mAMapLocationListenerProxys.add(aMapLocationListenerProxy);
    }

    public void addBDAbsLocationListenerProxy(BDAbsLocationListenerProxy bdAbsLocationListenerProxy) {
        this.mBDAbsLocationListenerProxys.add(bdAbsLocationListenerProxy);
    }

    public void addBDLocationListenerProxy(BDLocationListenerProxy bdLocationListenerProxy) {
        this.mBDLocationListenerProxys.add(bdLocationListenerProxy);
    }

    public void addTencentLocationListenerProxy(TencentLocationListenerProxy tencentLocationListenerProxy) {
        this.mTencentLocationListenerProxys.add(tencentLocationListenerProxy);
    }


    public void removeAMapLocationListener(AMapLocationListener listener) {
        for (Iterator<AMapLocationListenerProxy> it = mAMapLocationListenerProxys.iterator(); it.hasNext(); ) {
            AMapLocationListenerProxy proxy = it.next();
            if (proxy.aMapLocationListener == listener) {
                it.remove();
            }
        }
    }

    public void removeTencentLocationListener(TencentLocationListener listener) {
        for (Iterator<TencentLocationListenerProxy> it = mTencentLocationListenerProxys.iterator(); it.hasNext(); ) {
            TencentLocationListenerProxy proxy = it.next();
            if (proxy.mTencentLocationListener == listener) {
                it.remove();
            }
        }
    }


    public void removeBDLocationListener(BDLocationListener listener) {
        for (Iterator<BDLocationListenerProxy> it = mBDLocationListenerProxys.iterator(); it.hasNext(); ) {
            BDLocationListenerProxy proxy = it.next();
            if (proxy.mBdLocationListener == listener) {
                it.remove();
            }
        }
    }

    public void removeBDAbsLocationListener(BDAbstractLocationListener listener) {
        for (Iterator<BDAbsLocationListenerProxy> it = mBDAbsLocationListenerProxys.iterator(); it.hasNext(); ) {
            BDAbsLocationListenerProxy proxy = it.next();
            if (proxy.mBdLocationListener == listener) {
                it.remove();
            }
        }
    }

    public void clearProxy() {
        mAMapLocationListenerProxys.clear();
        mBDAbsLocationListenerProxys.clear();
        mBDLocationListenerProxys.clear();
        mTencentLocationListenerProxys.clear();
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
        if (location != null) {
            for (AMapLocationListenerProxy aMapLocationListenerProxy : mAMapLocationListenerProxys) {
                aMapLocationListenerProxy.onLocationChanged(LocationBuilder.toAMapLocation(location));
            }
        }
    }

    private void notifyBDAbsLocationListenerProxy(Location location) {
        if (location != null) {
            for (BDAbsLocationListenerProxy bdAbsLocationListenerProxy : mBDAbsLocationListenerProxys) {
                bdAbsLocationListenerProxy.onReceiveLocation(LocationBuilder.toBdLocation(location));
            }
        }
    }

    private void notifyBDLocationListenerProxy(Location location) {
        if (location != null) {
            for (BDLocationListenerProxy bdLocationListenerProxy : mBDLocationListenerProxys) {
                bdLocationListenerProxy.onReceiveLocation(LocationBuilder.toBdLocation(location));
            }
        }
    }

    private void notifyTencentLocationListenerProxy(Location location) {
        if (location != null) {
            for (TencentLocationListenerProxy tencentLocationListenerProxy : mTencentLocationListenerProxys) {
                tencentLocationListenerProxy.onLocationChanged(LocationBuilder.toTencentLocation(location), TencentLocation.ERROR_OK, "");
            }
        }
    }

    private static class Holder {
        final private static GpsMockProxyManager INSTANCE = new GpsMockProxyManager();
    }
}