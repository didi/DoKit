package com.didichuxing.doraemonkit.kit.mc.client

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2021/9/29-14:36
 * 描    述：
 * 修订历史：
 * ================================================
 */
enum class ClientSyncFailType {
    /**
     * 页面不一致
     */
    ACTIVITY,

    /**
     * 找不到指定的View
     */
    VIEW,

    /**
     * 模拟点击失败
     */
    PERFORM_CLICK,
    /**
     * 模拟获取焦点失败
     */
    PERFORM_FOCUSED,

}