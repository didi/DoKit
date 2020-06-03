package com.didichuxing.doraemonkit.kit.core

import android.app.Activity
import android.os.Bundle

/**
 * Created by jintai on 2019/9/16.
 * dokitView intent
 */
class DokitIntent(val targetClass: Class<out AbsDokitView?>) {

    var bundle: Bundle? = null

    /**
     * 赋值为类名 不接受对外赋值
     */
    val tag: String = targetClass.simpleName

    var activity: Activity? = null

    var mode = MODE_SINGLE_INSTANCE

    companion object {
        /**
         * 全局的悬浮窗
         */
        const val MODE_SINGLE_INSTANCE = 1

        /**
         * 只在页面创建时显示 页面resume时不恢复
         */
        const val MODE_ONCE = 2
    }


}