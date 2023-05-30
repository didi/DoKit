package com.didichuxing.doraemonkit.plugin.stack_method

import java.util.*

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020/5/20-16:58
 * 描    述：
 * 修订历史：
 * ================================================
 */
object MethodStackNodeUtil {


    val METHOD_STACK_KEYS: MutableList<MutableSet<String>> by lazy {
        Collections.synchronizedList(mutableListOf<MutableSet<String>>())
    }


    fun addMethodStackNode(level: Int, methodStackNode: MethodStackNode) {
        val key = "${methodStackNode.className}&${methodStackNode.methodName}&${methodStackNode.desc}"
        METHOD_STACK_KEYS[level].add(key)

    }


}
