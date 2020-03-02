package com.didichuxing.doraemonkit.aop;

import android.app.Activity;
import android.app.Application;
import android.app.Service;
import android.support.v4.app.Fragment;
import android.util.Log;

import java.util.concurrent.ConcurrentHashMap;

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020/2/29-15:31
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class MethodCostUtil {
    private static ConcurrentHashMap<String, Long> METHOD_COSTS = new ConcurrentHashMap<>();
    private static final String TAG = "MethodCostUtil";


    /**
     * 静态内部类单例
     */
    private static class Holder {
        private static MethodCostUtil INSTANCE = new MethodCostUtil();
    }

    public static MethodCostUtil getInstance() {
        return MethodCostUtil.Holder.INSTANCE;
    }

    public synchronized void recodeMethodCostStart(String methodName) {
        if (METHOD_COSTS == null) {
            return;
        }
        METHOD_COSTS.put(methodName, System.currentTimeMillis());
    }

    /**
     * @param thresholdTime 预设的值 单位为us 1000us = 1ms
     * @param methodName
     */
    public void recodeMethodCostEnd(int thresholdTime, String methodName) {
        if (METHOD_COSTS == null) {
            return;
        }
        synchronized (MethodCostUtil.class) {
            if (METHOD_COSTS.containsKey(methodName)) {
                long startTime = METHOD_COSTS.get(methodName);
                int costTime = (int) (System.currentTimeMillis() - startTime);
                METHOD_COSTS.remove(methodName);
                //如果该方法的执行时间大于1ms 则记录
                if (costTime >= thresholdTime) {
                    String threadName = Thread.currentThread().getName();
                    Log.i(TAG, "methodName===>" + methodName + "  threadName==>" + threadName + "  thresholdTime===>" + thresholdTime + "   costTime===>" + costTime);
                    StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
                    if (stackTraceElements.length > 3) {
                        for (int i = 3; i < stackTraceElements.length; i++) {
                            Log.i(TAG, "\tat " + stackTraceElements[i].toString());
                        }
                    }
                }
            }
        }

    }


}
