package com.didichuxing.doraemonkit.gps_mock.map;

import android.location.Location;
import android.location.LocationManager;

import com.amap.api.maps.LocationSource;
import com.didichuxing.doraemonkit.gps_mock.gpsmock.GpsMockManager;
import com.didichuxing.doraemonkit.util.CoordinateUtils;
import com.didichuxing.doraemonkit.util.LogHelper;

import java.util.Arrays;

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
            double[] res = CoordinateUtils.bd09ToGcj02(GpsMockManager.getInstance().getLongitude(), GpsMockManager.getInstance().getLatitude());
            LogHelper.d("onLocationChanged", "===amap===origin_loc==>" + location.toString()
                + "\n before_trans_loc==>" + GpsMockManager.getInstance().getLatitude() + "   lng==>" +GpsMockManager.getInstance().getLongitude()
            + "\n after_trans_loc==>" + Arrays.toString(res));
            location.setLatitude(res[1]);
            location.setLongitude(res[0]);
            location.setProvider(LocationManager.GPS_PROVIDER);
        }
        if (mOnLocationChangedListener != null) {
            mOnLocationChangedListener.onLocationChanged(location);
        }
    }
}
