package com.didichuxing.doraemonkit.aop

import android.app.Activity
import android.app.Application
import android.app.Service
import android.os.SystemClock
import android.util.Log
import com.didichuxing.doraemonkit.aop.method_stack.StaticMethodObject
import com.didichuxing.doraemonkit.kit.timecounter.TimeCounterManager
import java.util.concurrent.ConcurrentHashMap

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020/2/29-15:31
 * 描    述：全局的函数耗时工具类
 * 修订历史：
 * ================================================
 */
public object MethodCostUtil {
    private const val TAG = "DOKIT_SLOW_METHOD"

    /**
     * 用来标识是静态函数对象
     */
    private val staticMethodObject: StaticMethodObject by lazy {
        StaticMethodObject()
    }

    /**
     * key className&method
     */
    private val METHOD_COSTS: ConcurrentHashMap<String, Long?> by lazy { ConcurrentHashMap<String, Long?>() }

    @Synchronized
    fun recodeObjectMethodCostStart(thresholdTime: Int, methodName: String, classObj: Any?) {
        try {
            METHOD_COSTS[methodName] = SystemClock.elapsedRealtime()
            if (classObj is Application) {
                val methods = methodName.split("&".toRegex()).toTypedArray()
                if (methods.size == 2) {
                    if (methods[1] == "onCreate") {
                        TimeCounterManager.get().onAppCreateStart()
                    }
                    if (methods[1] == "attachBaseContext") {
                        TimeCounterManager.get().onAppAttachBaseContextStart()
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun recodeStaticMethodCostStart(thresholdTime: Int, methodName: String) {
        recodeObjectMethodCostStart(thresholdTime, methodName, staticMethodObject)

    }

    /**
     * 对象方法
     *
     * @param thresholdTime 预设的值 单位为us 1000us = 1ms
     * @param methodName
     * @param classObj      调用该函数的对象
     */
    fun recodeObjectMethodCostEnd(thresholdTime: Int, methodName: String, classObj: Any?) {
        synchronized(MethodCostUtil::class.java) {
            try {
                if (METHOD_COSTS.containsKey(methodName)) {
                    val startTime = METHOD_COSTS[methodName]!!
                    val costTime = (SystemClock.elapsedRealtime() - startTime).toInt()
                    METHOD_COSTS.remove(methodName)
                    if (classObj is Application) {
                        //Application 启动时间统计
                        val methods = methodName.split("&".toRegex()).toTypedArray()
                        if (methods.size == 2) {
                            if (methods[1] == "onCreate") {
                                TimeCounterManager.get().onAppCreateEnd()
                            }
                            if (methods[1] == "attachBaseContext") {
                                TimeCounterManager.get().onAppAttachBaseContextEnd()
                            }
                        }
                        //printApplicationStartTime(methodName);
                    } else if (classObj is Activity) {
                        //Activity 启动时间统计
                        //printActivityStartTime(methodName);
                    } else if (classObj is Service) {
                        //service 启动时间统计
                    }


                    //如果该方法的执行时间大于1ms 则记录
                    if (costTime >= thresholdTime) {
                        val threadName = Thread.currentThread().name
                        Log.i(TAG, "================Dokit================")
                        Log.i(
                            TAG,
                            "\t methodName===>$methodName  threadName==>$threadName  thresholdTime===>$thresholdTime   costTime===>$costTime"
                        )
                        val stackTraceElements = Thread.currentThread().stackTrace
                        for (stackTraceElement in stackTraceElements) {
                            if (stackTraceElement.toString().contains("MethodCostUtil")) {
                                continue
                            }
                            if (stackTraceElement.toString()
                                    .contains("dalvik.system.VMStack.getThreadStackTrace")
                            ) {
                                continue
                            }
                            if (stackTraceElement.toString()
                                    .contains("java.lang.Thread.getStackTrace")
                            ) {
                                continue
                            }
                            Log.i(TAG, "\tat $stackTraceElement")
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun printApplicationStartTime(methodName: String) {
        Log.i(TAG, "================Dokit Application start================")
        val stackTraceElements = Thread.currentThread().stackTrace
        for (stackTraceElement in stackTraceElements) {
            if (stackTraceElement.toString().contains("MethodCostUtil")) {
                continue
            }
            if (stackTraceElement.toString()
                    .contains("dalvik.system.VMStack.getThreadStackTrace")
            ) {
                continue
            }
            if (stackTraceElement.toString().contains("java.lang.Thread.getStackTrace")) {
                continue
            }
            Log.i(TAG, "\tat $stackTraceElement")
        }
        Log.i(TAG, "================Dokit Application  end================")
        Log.i(TAG, "\n")
    }

    private fun printActivityStartTime(methodName: String) {
        Log.i(TAG, "================Dokit Activity start================")
        val stackTraceElements = Thread.currentThread().stackTrace
        for (stackTraceElement in stackTraceElements) {
            if (stackTraceElement.toString().contains("MethodCostUtil")) {
                continue
            }
            if (stackTraceElement.toString()
                    .contains("dalvik.system.VMStack.getThreadStackTrace")
            ) {
                continue
            }
            if (stackTraceElement.toString().contains("java.lang.Thread.getStackTrace")) {
                continue
            }
            Log.i(TAG, "\tat $stackTraceElement")
        }
        Log.i(TAG, "================Dokit Activity end================")
        Log.i(TAG, "\n")
    }

    /**
     * 静态方法
     *
     * @param thresholdTime 预设的值 单位为us 1000us = 1ms
     * @param methodName
     */
    fun recodeStaticMethodCostEnd(thresholdTime: Int, methodName: String) {
        recodeObjectMethodCostEnd(thresholdTime, methodName, staticMethodObject)
    }


}