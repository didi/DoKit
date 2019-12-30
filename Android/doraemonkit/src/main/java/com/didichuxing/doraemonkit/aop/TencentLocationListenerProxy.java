package com.didichuxing.doraemonkit.aop;

import com.blankj.utilcode.util.ReflectUtils;
import com.didichuxing.doraemonkit.kit.gpsmock.GpsMockManager;
import com.didichuxing.doraemonkit.util.LogHelper;
import com.tencent.map.geolocation.TencentLocation;
import com.tencent.map.geolocation.TencentLocationListener;
import com.tencent.map.geolocation.TencentLocationUtils;

import java.lang.reflect.Field;

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
    TencentLocationListener tencentLocationListener;

    public TencentLocationListenerProxy(TencentLocationListener tencentLocationListener) {
        this.tencentLocationListener = tencentLocationListener;
    }

    @Override
    public void onLocationChanged(TencentLocation tencentLocation, int i, String s) {
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
        if (tencentLocationListener != null) {
            tencentLocationListener.onLocationChanged(tencentLocation, i, s);
        }
    }

    @Override
    public void onStatusUpdate(String s, int i, String s1) {
        if (tencentLocationListener != null) {
            tencentLocationListener.onStatusUpdate(s, i, s);
        }
    }


}
