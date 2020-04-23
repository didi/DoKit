package com.didichuxing.doraemonkit.aop.method_stack;

import android.util.Log;

import com.didichuxing.doraemonkit.aop.MethodCostUtil;

import java.util.concurrent.ConcurrentHashMap;

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020/4/22-15:44
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class MethodStackUtil {
    /**
     * key className&methodName
     */
    private static ConcurrentHashMap<String, MethodInvokNode> METHOD_STACKS = new ConcurrentHashMap<>();
    private static final String TAG = "MethodStackUtil";


    /**
     * 静态内部类单例
     */
    private static class Holder {
        private static MethodStackUtil INSTANCE = new MethodStackUtil();
    }

    public static MethodStackUtil getInstance() {
        return MethodStackUtil.Holder.INSTANCE;
    }

    /**
     * @param level
     * @param methodName
     * @param classObj   null 代表静态函数
     */
    public void recodeObjectMethodCostStart(int level, String className, String methodName, String desc, Object classObj) {
        if (METHOD_STACKS == null) {
            return;
        }
        try {
            MethodInvokNode methodInvokNode = new MethodInvokNode();
            methodInvokNode.setStartTimeMillis(System.currentTimeMillis());
            methodInvokNode.setCurrentThreadName(Thread.currentThread().getName());
            methodInvokNode.setClassName(className);
            methodInvokNode.setMethodName(methodName);

            METHOD_STACKS.put(String.format("%s&%s", className, methodName), methodInvokNode);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param level
     * @param className
     * @param methodName
     * @param desc
     * @param classObj   null 代表静态函数
     */
    public void recodeObjectMethodCostEnd(int level, String className, String methodName, String desc, Object classObj) {
        if (METHOD_STACKS == null) {
            return;
        }
        synchronized (MethodCostUtil.class) {
            try {
                if (METHOD_STACKS.containsKey(String.format("%s&%s", className, methodName))) {
                    MethodInvokNode methodInvokNode = METHOD_STACKS.get(String.format("%s&%s", className, methodName));
                    methodInvokNode.setEndTimeMillis(System.currentTimeMillis());
                    if (level == 0) {
                        //代表一整个函数调用结束
                    }


                    Log.i(TAG, "================Dokit================");
                    Log.i(TAG, "\t methodName===>" + String.format("%s&%s&%s", className, methodName, desc) + "  threadName==>" + methodInvokNode.getCurrentThreadName() + "   costTime===>" + methodInvokNode.getCostTimeMillis());
                    StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
                    for (StackTraceElement stackTraceElement : stackTraceElements) {
                        if (stackTraceElement.toString().contains("MethodStackUtil")) {
                            continue;
                        }
                        if (stackTraceElement.toString().contains("dalvik.system.VMStack.getThreadStackTrace")) {
                            continue;
                        }
                        if (stackTraceElement.toString().contains("java.lang.Thread.getStackTrace")) {
                            continue;
                        }

                        Log.i(TAG, "\tat " + stackTraceElement.getClassName() + "&" + stackTraceElement.getMethodName());
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }


    public void recodeStaticMethodCostStart(int level, String className, String methodName, String desc) {
        recodeObjectMethodCostStart(level, className, methodName, desc, null);
    }


    public void recodeStaticMethodCostEnd(int level, String className, String methodName, String desc) {
        recodeObjectMethodCostEnd(level, className, methodName, desc, null);
    }
}
