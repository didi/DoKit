package com.didichuxing.doraemonkit.config

import com.blankj.utilcode.util.SPUtils
import com.didichuxing.doraemonkit.constant.SharedPrefsKey

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020-01-06-11:37
 * 描    述：
 * 修订历史：
 * ================================================
 */
object GlobalConfig {

    /**
     * 获得app 健康体检功能的本地状态
     */
    /**
     * 设置健康体检
     *
     * @param isRunning
     */
    var appHealth: Boolean
        get() = SPUtils.getInstance().getBoolean(SharedPrefsKey.APP_HEALTH, false)
        set(isRunning) {
            SPUtils.getInstance().put(SharedPrefsKey.APP_HEALTH, isRunning)
        }
}