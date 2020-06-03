package com.didichuxing.doraemondemo;

import android.util.Log;

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
    private static final String TAG = "AopTest";

//    public void test() {
//        //MethodStackUtil.getInstance().recodeObjectMethodCostStart(1, "AopTest", "test", "desc", this);
//        Log.i(TAG, "================");
//        //MethodStackUtil.getInstance().recodeObjectMethodCostEnd(2, "AopTest", "test", "desc", this);
//    }


    public void sleepMethod() {
        long begin = System.currentTimeMillis();
        Log.i(TAG, "我是耗时函数");
        long costTime = System.currentTimeMillis() - begin;
    }


}
