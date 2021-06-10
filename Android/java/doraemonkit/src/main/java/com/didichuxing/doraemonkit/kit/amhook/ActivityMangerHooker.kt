package com.didichuxing.doraemonkit.kit.amhook

import android.content.Context
import android.os.IBinder
import com.didichuxing.doraemonkit.kit.gpsmock.BaseServiceHooker

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2021/6/10-14:24
 * 描    述：ActivityManagerService hook
 * 修订历史：
 * ================================================
 */
class ActivityMangerHooker : BaseServiceHooker() {
    override fun getServiceName(): String {
        return Context.ACTIVITY_SERVICE
    }

    override fun getStubName(): String {
        return ""
    }

    override fun getMethodHandlers(): MutableMap<String, MethodHandler> {
        TODO("Not yet implemented")
    }

    override fun replaceBinder(context: Context?, proxy: IBinder?) {
        TODO("Not yet implemented")
    }
}