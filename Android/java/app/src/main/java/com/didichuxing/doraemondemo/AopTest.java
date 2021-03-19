package com.didichuxing.doraemondemo;

import android.content.Context;

import com.amap.api.location.AMapLocation;
import com.didichuxing.doraemonkit.aop.map.AMapLocationClientProxy;
import com.didichuxing.doraemonkit.util.LogHelper;
import com.loc.d;

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020/4/22-11:38
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class AopTest {



    public String getLastKnownLocation() {
        String s = getDoKit();
        s.toString();
        return s;
    }


    public String getDoKit() {
        return "DoKit";
    }

}
