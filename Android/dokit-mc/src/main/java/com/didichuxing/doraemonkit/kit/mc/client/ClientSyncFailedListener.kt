package com.didichuxing.doraemonkit.kit.mc.client

import android.view.View
import com.didichuxing.doraemonkit.kit.mc.all.view_info.ViewC12c
import java.lang.Exception

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2021/9/2-11:05
 * 描    述：从机同步执行失败
 * 修订历史：
 * ================================================
 */
interface ClientSyncFailedListener {
    fun callBack(type: ClientSyncFailType, viewC12c: ViewC12c?, view: View?)
}