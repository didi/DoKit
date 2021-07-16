package com.didichuxing.doraemonkit.kit.mc.all.view_info

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020/12/8-16:13
 * 描    述：View的特征
 * 修订历史：
 * ================================================
 */

data class ViewC12c(
    /**
     * 通用事件类型
     */
    val commEventType: Int = -9999,
    val customEventType: String = "",
    val customParams: String = "",
    /**
     * window index
     */
    val viewRootImplIndex: Int = -1,
    /**
     * view 相对于window的path
     */
    val viewPaths: MutableList<SystemViewInfo>? = null,
    val accEventInfo: AccEventInfo? = null,
    var text: String? = "",
    /**
     * dokit 悬浮窗信息
     */
    val dokitViewPosInfo: DokitViewInfo?=null
)