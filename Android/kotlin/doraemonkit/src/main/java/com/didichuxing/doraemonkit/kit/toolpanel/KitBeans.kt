package com.didichuxing.doraemonkit.kit.toolpanel

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020/5/6-16:48
 * 描    述：自定义拖拽kit 数据持久化做准备
 * 修订历史：
 * ================================================
 */

data class KitGroupBean(var groupId: String, var kits: MutableList<KitBean>)
data class KitBean(var allClassName: String, var checked: Boolean, var innerKitId: String = "")
