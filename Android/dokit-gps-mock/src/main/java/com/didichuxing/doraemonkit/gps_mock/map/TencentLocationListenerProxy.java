package com.didichuxing.doraemonkit.gps_mock.map;

import com.didichuxing.doraemonkit.gps_mock.gpsmock.GpsMockManager;
import com.didichuxing.doraemonkit.gps_mock.gpsmock.GpsMockProxyManager;
import com.didichuxing.doraemonkit.util.ReflectUtils;
import com.tencent.map.geolocation.TencentLocation;
import com.tencent.map.geolocation.TencentLocationListener;

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2019-12-15-16:18
 * 描    述：腾讯TencentLocationListenerProxy 通过ASM代码动态插入
 * 修订历史：
 * ================================================
 */
public class TencentLocationListenerProxy implements TencentLocationListener {
    private static final String TAG = "TencentLocationListenerProxy";
    public TencentLocationListener mTencentLocationListener;

    public TencentLocationListenerProxy(TencentLocationListener tencentLocationListener) {
        this.mTencentLocationListener = tencentLocationListener;
        GpsMockProxyManager.INSTANCE.addTencentLocationListenerProxy(this);
    }

    /**
     * @param tencentLocation - 新的位置, *可能*来自缓存. 定位失败时 location 无效或者为 null
     * @param error           - 错误码, 仅当错误码为 TencentLocation.ERROR_OK 时表示定位成功, 为其他值时表示定位失败
     * @param reason          - 错误描述, 简要描述错误原因
     */
    @Override
    public void onLocationChanged(TencentLocation tencentLocation, int error, String reason) {
        if (GpsMockManager.getInstance().isMocking()) {
            try {
                //tencentLocation 的 对象类型为TxLocation
                //LogHelper.i(TAG, "matched==onLocationChanged==" + tencentLocation.toString());
                //b 为fb类型
                Object b = ReflectUtils.reflect(tencentLocation).field("b").get();
                //a 为lat
                ReflectUtils.reflect(b).field("a", GpsMockManager.getInstance().getLatitude());
                //b 为lng
                ReflectUtils.reflect(b).field("b", GpsMockManager.getInstance().getLongitude());
                //LogHelper.i(TAG, "b===>" + b.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        if (mTencentLocationListener != null) {
            mTencentLocationListener.onLocationChanged(tencentLocation, error, reason);
        }
    }

    @Override
    public void onStatusUpdate(String s, int i, String s1) {
        if (mTencentLocationListener != null) {
            mTencentLocationListener.onStatusUpdate(s, i, s);
        }
    }


}
