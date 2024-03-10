package com.didichuxing.doraemonkit.gps_mock.map;

import androidx.annotation.NonNull;

import com.amap.api.navi.AMapNaviListener;
import com.amap.api.navi.model.AMapCalcRouteResult;
import com.amap.api.navi.model.AMapNaviLocation;
import com.didichuxing.doraemonkit.gps_mock.gpsmock.GpsMockProxyManager;

/**
 * didi Create on 2023/3/28 .
 * <p>
 * Copyright (c) 2023/3/28 by didiglobal.com.
 *
 * @author <a href="realonlyone@126.com">zhangjun</a>
 * @version 1.0
 * @Date 2023/3/28 8:13 下午
 * @Description 用一句话说明文件功能
 */

public class AMapNaviListenerProxy extends AMapNaviListenerProxyNoOp {

    public AMapNaviListener aMapNaviListener;
    private AMapNaviListenerProxyDelegate delegate;

    public AMapNaviListenerProxy(@NonNull AMapNaviListener naviListener) {
        super(naviListener);
        this.aMapNaviListener = naviListener;
        delegate = new AMapNaviListenerProxyDelegate();
        GpsMockProxyManager.INSTANCE.addAMapNaviListenerProxy(this);
    }

    @Override
    public void onLocationChange(AMapNaviLocation aMapNaviLocation) {
        delegate.onLocationChange(aMapNaviLocation);
        super.onLocationChange(aMapNaviLocation);
    }

    @Override
    public void onCalculateRouteSuccess(AMapCalcRouteResult aMapCalcRouteResult) {
        delegate.onCalculateRouteSuccess(aMapCalcRouteResult);
        super.onCalculateRouteSuccess(aMapCalcRouteResult);
    }
}
