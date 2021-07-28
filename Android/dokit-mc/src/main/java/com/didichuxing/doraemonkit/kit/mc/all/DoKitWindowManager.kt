package com.didichuxing.doraemonkit.kit.mc.all

import android.view.ViewParent
import com.didichuxing.doraemonkit.kit.mc.util.McHookUtil

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020/12/14-20:06
 * 描    述：
 * 修订历史：
 * ================================================
 */
public object DoKitWindowManager {
    var ROOT_VIEWS: List<ViewParent>? = null

    fun hookWindowManagerGlobal() {
        ROOT_VIEWS = McHookUtil.getRootViewsFromWmg()
    }
}