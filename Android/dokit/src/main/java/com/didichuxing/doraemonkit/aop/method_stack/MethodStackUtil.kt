package com.didichuxing.doraemonkit.aop.method_stack

import android.app.Application
import android.util.Log
import com.didichuxing.doraemonkit.util.GsonUtils
import com.didichuxing.doraemonkit.util.LogUtils
import com.didichuxing.doraemonkit.aop.MethodCostUtil
import com.didichuxing.doraemonkit.kit.timecounter.TimeCounterManager
import java.util.*
import java.util.concurrent.ConcurrentHashMap

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020/4/22-15:44
 * 描    述：自定义函数入口调用栈的耗时工具类
 * 修订历史：
 * ================================================
 */
public object MethodStackUtil {
    /**
     * key className&methodName
     */
    private val METHOD_STACKS: MutableList<ConcurrentHashMap<String, MethodInvokNode>> by lazy {
        Collections.synchronizedList(mutableListOf<ConcurrentHashMap<String, MethodInvokNode>>())
    }

    /**
     * 用来标识是静态函数对象
     */
    private val staticMethodObject: StaticMethodObject by lazy {
        StaticMethodObject()
    }


    private fun createMethodStackList(totalLevel: Int) {
        if (METHOD_STACKS.size == totalLevel) {
            return
        }
        METHOD_STACKS.clear()
        for (index in 0 until totalLevel) {
            METHOD_STACKS.add(index, ConcurrentHashMap())
        }
    }

    /**
     * @param currentLevel
     * @param methodName
     * @param classObj   null 代表静态函数
     */
    fun recodeObjectMethodCostStart(
        totalLevel: Int,
        thresholdTime: Int,
        currentLevel: Int,
        className: String?,
        methodName: String,
        desc: String?,
        classObj: Any?
    ) {
        try {
            //先创建队列
            createMethodStackList(totalLevel)
            val methodInvokNode = MethodInvokNode()
            methodInvokNode.startTimeMillis = System.currentTimeMillis()
            methodInvokNode.currentThreadName = Thread.currentThread().name
            methodInvokNode.className = className
            methodInvokNode.methodName = methodName
            methodInvokNode.level = currentLevel
            METHOD_STACKS[currentLevel][String.format("%s&%s", className, methodName)] =
                methodInvokNode

            //特殊判定
            if (currentLevel == 0) {
                if (classObj is Application) {
                    if (methodName == "onCreate") {
                        TimeCounterManager.get().onAppCreateStart()
                    }
                    if (methodName == "attachBaseContext") {
                        TimeCounterManager.get().onAppAttachBaseContextStart()
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * @param currentLevel
     * @param className
     * @param methodName
     * @param desc
     * @param classObj   null 代表静态函数
     */
    fun recodeObjectMethodCostEnd(
        thresholdTime: Int,
        currentLevel: Int,
        className: String,
        methodName: String,
        desc: String?,
        classObj: Any?
    ) {
        synchronized(MethodStackUtil::class.java) {
            try {
                val methodInvokNode =
                    METHOD_STACKS[currentLevel][String.format("%s&%s", className, methodName)]
                if (methodInvokNode != null) {
                    methodInvokNode.setEndTimeMillis(System.currentTimeMillis())
                    bindNode(thresholdTime, currentLevel, methodInvokNode)
                }

                //打印函数调用栈
                if (currentLevel == 0) {
                    if (methodInvokNode != null) {
                        toStack(classObj is Application, methodInvokNode)
                    }
                    if (classObj is Application) {
                        //Application 启动时间统计
                        if (methodName == "onCreate") {
                            TimeCounterManager.get().onAppCreateEnd()
                        }
                        if (methodName == "attachBaseContext") {
                            TimeCounterManager.get().onAppAttachBaseContextEnd()
                        }
                    }

                    //移除对象
                    METHOD_STACKS[0].remove("$className&$methodName")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun getParentMethod(currentClassName: String?, currentMethodName: String?): String {
        val stackTraceElements = Thread.currentThread().stackTrace
        var index = 0
        for (i in stackTraceElements.indices) {
            val stackTraceElement = stackTraceElements[i]
            if (currentClassName == stackTraceElement.className && currentMethodName == stackTraceElement.methodName) {
                index = i
                break
            }
        }
        val parentStackTraceElement = stackTraceElements[index + 1]
        return String.format(
            "%s&%s",
            parentStackTraceElement.className,
            parentStackTraceElement.methodName
        )
    }

    private fun bindNode(thresholdTime: Int, currentLevel: Int, methodInvokNode: MethodInvokNode?) {
        if (methodInvokNode == null) {
            return
        }

        //过滤掉小于指定阈值的函数
        if (methodInvokNode.getCostTimeMillis() <= thresholdTime) {
            return
        }
        if (currentLevel >= 1) {
            val parentMethodNode = METHOD_STACKS[currentLevel - 1][getParentMethod(
                methodInvokNode.className,
                methodInvokNode.methodName
            )]
            if (parentMethodNode != null) {
                methodInvokNode.parent = parentMethodNode
                parentMethodNode.addChild(methodInvokNode)
            }
        }
    }

    fun recodeStaticMethodCostStart(
        totalLevel: Int,
        thresholdTime: Int,
        currentLevel: Int,
        className: String?,
        methodName: String,
        desc: String?
    ) {
        recodeObjectMethodCostStart(
            totalLevel,
            thresholdTime,
            currentLevel,
            className,
            methodName,
            desc,
            staticMethodObject
        )
    }

    fun recodeStaticMethodCostEnd(
        thresholdTime: Int,
        currentLevel: Int,
        className: String,
        methodName: String,
        desc: String?
    ) {
        recodeObjectMethodCostEnd(
            thresholdTime,
            currentLevel,
            className,
            methodName,
            desc,
            staticMethodObject
        )
    }

    private fun jsonTravel(
        methodStackBeans: MutableList<MethodStackBean>?,
        methodInvokNodes: List<MethodInvokNode>?
    ) {
        if (methodInvokNodes == null) {
            return
        }
        for (methodInvokNode in methodInvokNodes) {
            val methodStackBean = MethodStackBean()
            methodStackBean.setCostTime(methodInvokNode.getCostTimeMillis())
            methodStackBean.function = methodInvokNode.className + "&" + methodInvokNode.methodName
            methodStackBean.children = ArrayList()
            jsonTravel(methodStackBean.children, methodInvokNode.children)
            methodStackBeans?.add(methodStackBean)
        }
    }

    private fun stackTravel(
        stringBuilder: StringBuilder,
        methodInvokNodes: List<MethodInvokNode>?
    ) {
        if (methodInvokNodes == null) {
            return
        }
        for (methodInvokNode in methodInvokNodes) {
            stringBuilder.append(
                String.format(
                    "%s%s%s%s%s",
                    methodInvokNode.level,
                    SPACE_0,
                    methodInvokNode.getCostTimeMillis().toString() + "ms",
                    getSpaceString(methodInvokNode.level),
                    methodInvokNode.className + "&" + methodInvokNode.methodName
                )
            ).append("\n")
            stackTravel(stringBuilder, methodInvokNode.children)
        }
    }

    fun toJson() {
        val methodStackBeans: MutableList<MethodStackBean> = ArrayList()
        for (methodInvokNode in METHOD_STACKS[0].values) {
            val methodStackBean = MethodStackBean()
            methodStackBean.setCostTime(methodInvokNode.getCostTimeMillis())
            methodStackBean.function = methodInvokNode.className + "&" + methodInvokNode.methodName
            methodStackBean.children = ArrayList()
            jsonTravel(methodStackBean.children, methodInvokNode.children)
            methodStackBeans.add(methodStackBean)
        }
        val json = GsonUtils.toJson(methodStackBeans)
        LogUtils.json(json)
    }

    fun toStack(isAppStart: Boolean, methodInvokNode: MethodInvokNode) {
        val stringBuilder = StringBuilder()
        stringBuilder.append("=========DoKit函数调用栈==========").append("\n")
        stringBuilder.append(String.format("%s    %s    %s", "level", "time", "function"))
            .append("\n")
        stringBuilder.append(
            String.format(
                "%s%s%s%s%s",
                methodInvokNode.level,
                SPACE_0,
                methodInvokNode.getCostTimeMillis().toString() + "ms",
                getSpaceString(methodInvokNode.level),
                methodInvokNode.className + "&" + methodInvokNode.methodName
            )
        ).append("\n")
        stackTravel(stringBuilder, methodInvokNode.children)
        Log.i(TAG, stringBuilder.toString())
        if (isAppStart && methodInvokNode.level == 0) {
            if (methodInvokNode.methodName == "onCreate") {
                STR_APP_ON_CREATE = stringBuilder.toString()
            }
            if (methodInvokNode.methodName == "attachBaseContext") {
                STR_APP_ATTACH_BASECONTEXT = stringBuilder.toString()
            }
        }
    }

    private fun getSpaceString(level: Int): String {
        return when (level) {
            0 -> SPACE_0
            1 -> SPACE_1
            2 -> SPACE_2
            3 -> SPACE_3
            4 -> SPACE_4
            5 -> SPACE_5
            6 -> SPACE_6
            7 -> SPACE_7
            else -> SPACE_0
        }

    }

    private const val TAG = "DOKIT_SLOW_METHOD"


    private const val SPACE_0 = "********"
    private const val SPACE_1 = "*************"
    private const val SPACE_2 = "*****************"
    private const val SPACE_3 = "*********************"
    private const val SPACE_4 = "*************************"
    private const val SPACE_5 = "*****************************"
    private const val SPACE_6 = "*********************************"
    private const val SPACE_7 = "*************************************"

    @JvmField
    var STR_APP_ON_CREATE: String? = null

    @JvmField
    var STR_APP_ATTACH_BASECONTEXT: String? = null

}