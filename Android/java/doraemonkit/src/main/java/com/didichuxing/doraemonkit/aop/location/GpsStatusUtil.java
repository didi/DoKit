package com.didichuxing.doraemonkit.aop.location;

import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.util.SparseArray;

import com.didichuxing.doraemonkit.kit.gpsmock.GpsMockManager;

import java.lang.reflect.Field;

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：4/25/21-20:34
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class GpsStatusUtil {
    /**
     * status 包装
     *
     * @param status
     * @return new GpsStatus
     */
    public static GpsStatus wrap(GpsStatus status) {
        if (GpsMockManager.getInstance().isMocking()) {
            //在这里对GpsStatus进行修改
            modifyGpsStatus(status);
            return status;
        }
        return status;
    }

    public static void modifyGpsStatus(GpsStatus gpsStatus) {
        try {
            Class<GpsStatus> gpsStatusCls = (Class<GpsStatus>) gpsStatus.getClass();
            Field mSatellitesField = gpsStatusCls.getField("mSatellites");
            mSatellitesField.setAccessible(true);
            SparseArray<GpsSatellite> mSatellites = new SparseArray<>();

            Class<? extends GpsSatellite> satliteClass = (Class<? extends GpsSatellite>) Class.forName("android.location.GpsSatellite");
            GpsSatellite satellite = satliteClass.newInstance();
            Field mUsedInFixField = satliteClass.getField("mUsedInFix");
            mUsedInFixField.setAccessible(true);
            mUsedInFixField.set(satellite, true);
            Field mPrnField = satliteClass.getField("mPrn");
            mPrnField.setAccessible(true);
            mPrnField.setInt(satellite, -5);

            mSatellites.append(0, satellite);
            mSatellites.append(0, satellite);
            mSatellites.append(0, satellite);
            mSatellites.append(0, satellite);
            mSatellites.append(0, satellite);

            mSatellitesField.set(gpsStatus, mSatellites);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
