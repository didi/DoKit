package com.didichuxing.doraemonkit.aop.location;

import android.location.GpsStatus;

import com.didichuxing.doraemonkit.kit.gpsmock.GpsMockManager;

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
            return status;
        }
        return status;
    }
}
