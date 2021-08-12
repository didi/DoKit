package com.didichuxing.doraemonkit.aop.map;

import android.location.Location;

import com.amap.api.maps.LocationSource;
import com.didichuxing.doraemonkit.kit.gpsmock.GpsMockManager;
import com.didichuxing.doraemonkit.util.LogHelper;

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：3/25/21-16:08
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class AMapLocationChangedListenerProxy implements LocationSource.OnLocationChangedListener {
    private static final String TAG = "AMapLocationChangedListenerProxy";
    LocationSource.OnLocationChangedListener mOnLocationChangedListener;

    public AMapLocationChangedListenerProxy(LocationSource.OnLocationChangedListener mOnLocationChangedListener) {
        this.mOnLocationChangedListener = mOnLocationChangedListener;
    }


    @Override
    public void onLocationChanged(Location location) {

        if (GpsMockManager.getInstance().isMocking()) {
            location.setLatitude(GpsMockManager.getInstance().getLatitude());
            location.setLongitude(GpsMockManager.getInstance().getLongitude());
        }
        LogHelper.i(TAG, "===onLocationChanged====");
        if (mOnLocationChangedListener != null) {
            mOnLocationChangedListener.onLocationChanged(location);
        }
    }
}
