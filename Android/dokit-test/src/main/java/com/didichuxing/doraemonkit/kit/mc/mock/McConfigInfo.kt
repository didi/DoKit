package com.didichuxing.doraemonkit.kit.mc.all.data

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2021/7/6-17:12
 * 描    述：
 * 修订历史：
 * ================================================
 */
data class McConfigInfo(val multiControl: MultiControl? = null)
data class MultiControl(val exclude: List<String>? = null)
