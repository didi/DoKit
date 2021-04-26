package com.didichuxing.doraemonkit.aop.location;

import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.util.SparseArray;

import com.didichuxing.doraemonkit.kit.gpsmock.GpsMockManager;

import java.lang.reflect.Constructor;
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
            Field mSatellitesField = gpsStatusCls.getDeclaredField("mSatellites");
            mSatellitesField.setAccessible(true);
            checkSatellite();
            mSatellitesField.set(gpsStatus, sSatellites);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static SparseArray<GpsSatellite> sSatellites;

    public static void checkSatellite() throws ClassNotFoundException, NoSuchMethodException, NoSuchFieldException, IllegalAccessException, InstantiationException, java.lang.reflect.InvocationTargetException {
        if (sSatellites == null) {
            synchronized (GpsStatusUtil.class) {
                if (sSatellites == null) {
                    sSatellites = new SparseArray<>();
                    Class<? extends GpsSatellite> satliteClass = (Class<? extends GpsSatellite>) Class.forName("android.location.GpsSatellite");
                    Constructor<? extends GpsSatellite> satliteClassConstructor = satliteClass.getDeclaredConstructor(int.class);

                    Field mUsedInFixField = satliteClass.getDeclaredField("mUsedInFix");
                    mUsedInFixField.setAccessible(true);
                    Field mValidField = satliteClass.getDeclaredField("mValid");
                    mValidField.setAccessible(true);
                    Field mHasEphemerisField = satliteClass.getDeclaredField("mHasEphemeris");
                    mHasEphemerisField.setAccessible(true);
                    Field mHasAlmanacField = satliteClass.getDeclaredField("mHasAlmanac");
                    mHasAlmanacField.setAccessible(true);

                    GpsSatellite satellite = satliteClassConstructor.newInstance(-5);
                    mUsedInFixField.set(satellite, true);
                    mValidField.set(satellite, true);
                    mHasEphemerisField.set(satellite, true);
                    mHasAlmanacField.set(satellite, true);

                    for (int i = 0; i < 12; i++) {
                        sSatellites.append(i, satellite);
                    }
                }

            }
        }
    }
}
