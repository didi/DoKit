package com.didichuxing.doraemonkit.aop;

import android.app.Activity;
import android.app.Application;
import android.app.Service;
import android.util.Log;

import com.didichuxing.doraemonkit.kit.timecounter.TimeCounterManager;

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
    /**
     * key className&method
     */
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

    public synchronized void recodeObjectMethodCostStart(int thresholdTime, String methodName, Object classObj) {
        if (METHOD_COSTS == null) {
            return;
        }
        try {
            METHOD_COSTS.put(methodName, System.currentTimeMillis());
            if (classObj instanceof Application) {
                String[] methods = methodName.split("&");
                if (methods.length == 2) {
                    if (methods[1].equals("onCreate")) {
                        TimeCounterManager.get().onAppCreateStart();
                    }

                    if (methods[1].equals("attachBaseContext")) {
                        TimeCounterManager.get().onAppAttachBaseContextStart();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public synchronized void recodeStaticMethodCostStart(int thresholdTime, String methodName) {
        if (METHOD_COSTS == null) {
            return;
        }
        try {
            METHOD_COSTS.put(methodName, System.currentTimeMillis());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 对象方法
     *
     * @param thresholdTime 预设的值 单位为us 1000us = 1ms
     * @param methodName
     * @param classObj      调用该函数的对象
     */
    public void recodeObjectMethodCostEnd(int thresholdTime, String methodName, Object classObj) {
        if (METHOD_COSTS == null) {
            return;
        }
        synchronized (MethodCostUtil.class) {
            try {
                if (METHOD_COSTS.containsKey(methodName)) {
                    long startTime = METHOD_COSTS.get(methodName);
                    int costTime = (int) (System.currentTimeMillis() - startTime);
                    METHOD_COSTS.remove(methodName);
                    if (classObj instanceof Application) {
                        //Application 启动时间统计
                        String[] methods = methodName.split("&");
                        if (methods.length == 2) {
                            if (methods[1].equals("onCreate")) {
                                TimeCounterManager.get().onAppCreateEnd();
                            }
                            if (methods[1].equals("attachBaseContext")) {
                                TimeCounterManager.get().onAppAttachBaseContextEnd();
                            }
                        }
                        //printApplicationStartTime(methodName);
                    } else if (classObj instanceof Activity) {
                        //Activity 启动时间统计
                        //printActivityStartTime(methodName);
                    } else if (classObj instanceof Service) {
                        //service 启动时间统计
                    }


                    //如果该方法的执行时间大于1ms 则记录
                    if (costTime >= thresholdTime) {
                        String threadName = Thread.currentThread().getName();
                        Log.i(TAG, "================Dokit================");
                        Log.i(TAG, "\t methodName===>" + methodName + "  threadName==>" + threadName + "  thresholdTime===>" + thresholdTime + "   costTime===>" + costTime);
                        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
                        for (StackTraceElement stackTraceElement : stackTraceElements) {
                            if (stackTraceElement.toString().contains("MethodCostUtil")) {
                                continue;
                            }
                            if (stackTraceElement.toString().contains("dalvik.system.VMStack.getThreadStackTrace")) {
                                continue;
                            }
                            if (stackTraceElement.toString().contains("java.lang.Thread.getStackTrace")) {
                                continue;
                            }

                            Log.i(TAG, "\tat " + stackTraceElement.toString());
                        }

                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }

    private void printApplicationStartTime(String methodName) {
        Log.i(TAG, "================Dokit Application start================");
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        for (StackTraceElement stackTraceElement : stackTraceElements) {
            if (stackTraceElement.toString().contains("MethodCostUtil")) {
                continue;
            }
            if (stackTraceElement.toString().contains("dalvik.system.VMStack.getThreadStackTrace")) {
                continue;
            }
            if (stackTraceElement.toString().contains("java.lang.Thread.getStackTrace")) {
                continue;
            }

            Log.i(TAG, "\tat " + stackTraceElement.toString());
        }

        Log.i(TAG, "================Dokit Application  end================");
        Log.i(TAG, "\n");
    }


    private void printActivityStartTime(String methodName) {
        Log.i(TAG, "================Dokit Activity start================");
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        for (StackTraceElement stackTraceElement : stackTraceElements) {
            if (stackTraceElement.toString().contains("MethodCostUtil")) {
                continue;
            }
            if (stackTraceElement.toString().contains("dalvik.system.VMStack.getThreadStackTrace")) {
                continue;
            }
            if (stackTraceElement.toString().contains("java.lang.Thread.getStackTrace")) {
                continue;
            }
            Log.i(TAG, "\tat " + stackTraceElement.toString());
        }

        Log.i(TAG, "================Dokit Activity end================");
        Log.i(TAG, "\n");
    }

    /**
     * 静态方法
     *
     * @param thresholdTime 预设的值 单位为us 1000us = 1ms
     * @param methodName
     */
    public void recodeStaticMethodCostEnd(int thresholdTime, String methodName) {
        if (METHOD_COSTS == null) {
            return;
        }
        synchronized (MethodCostUtil.class) {
            try {
                if (METHOD_COSTS.containsKey(methodName)) {
                    long startTime = METHOD_COSTS.get(methodName);
                    int costTime = (int) (System.currentTimeMillis() - startTime);
                    METHOD_COSTS.remove(methodName);
                    //如果该方法的执行时间大于1ms 则记录
                    if (costTime >= thresholdTime) {
                        String threadName = Thread.currentThread().getName();
                        Log.i(TAG, "================Dokit================");
                        Log.i(TAG, "\t methodName===>" + methodName + "  threadName==>" + threadName + "  thresholdTime===>" + thresholdTime + "   costTime===>" + costTime);
                        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
                        for (StackTraceElement stackTraceElement : stackTraceElements) {
                            if (stackTraceElement.toString().contains("MethodCostUtil")) {
                                continue;
                            }
                            if (stackTraceElement.toString().contains("dalvik.system.VMStack.getThreadStackTrace")) {
                                continue;
                            }
                            if (stackTraceElement.toString().contains("java.lang.Thread.getStackTrace")) {
                                continue;
                            }

                            Log.i(TAG, "\tat " + stackTraceElement.toString());
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }


}
