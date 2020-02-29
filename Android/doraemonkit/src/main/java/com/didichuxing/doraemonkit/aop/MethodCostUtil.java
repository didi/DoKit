package com.didichuxing.doraemonkit.aop;

import android.util.Log;

import com.blankj.utilcode.util.NetworkUtils;

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

    public static synchronized void recodeMethodCostStart(String methodName) {
        METHOD_COSTS.put(methodName, System.currentTimeMillis());
    }

    /**
     * @param thresholdTime 预设的值
     * @param methodName
     * @param classObj
     */
    public static void recodeMethodCostEnd(int thresholdTime, String methodName, Object classObj) {
        if (METHOD_COSTS == null) {
            return;
        }
        synchronized (MethodCostUtil.class) {
            if (METHOD_COSTS.containsKey(methodName)) {
                long startTime = METHOD_COSTS.get(methodName);
                int costTime = (int) (System.currentTimeMillis() - startTime);
                METHOD_COSTS.remove(methodName);
                //如果该方法的执行时间大于10ms 则记录
                if (costTime >= thresholdTime) {
                    String threadName = Thread.currentThread().getName();
                    Log.i(TAG, "methodName===>" + methodName + "   classObj==>" + classObj + "  threadName==>" + threadName + "  thresholdTime===>" + thresholdTime + "   costTime===>" + costTime);
                }
            }
        }
    }
}
