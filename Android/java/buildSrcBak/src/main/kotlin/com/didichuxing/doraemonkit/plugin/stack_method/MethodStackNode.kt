package com.didichuxing.doraemonkit.plugin.stack_method

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020/5/20-16:50
 * 描    述：
 * 修订历史：
 * ================================================
 */
data class MethodStackNode(var level: Int,
                           var className: String,
                           var methodName: String,
                           var desc: String,
                           var parentClassName: String,
                           var parentMethodName: String,
                           var parentDesc: String)