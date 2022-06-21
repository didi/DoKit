package com.didichuxing.doraemonkit.gps_mock.map;

import com.amap.api.maps.LocationSource;
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
public class AMapLocationSourceProxy implements LocationSource {
    LocationSource mLocationSource;

    public AMapLocationSourceProxy(LocationSource mLocationSource) {
        this.mLocationSource = mLocationSource;
    }


    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        LogHelper.d("AMapLocationSourceProxy", "===amap===activate");
        if (mLocationSource != null) {
            onLocationChangedListener = new AMapLocationChangedListenerProxy(onLocationChangedListener);
            mLocationSource.activate(onLocationChangedListener);
        }
    }

    @Override
    public void deactivate() {
        if (mLocationSource != null) {
            mLocationSource.deactivate();
        }
    }
}
