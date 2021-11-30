package com.kronos.dokit.pthread

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.didichuxing.doraemonkit.DoKit
import com.didichuxing.doraemonkit.kit.AbstractKit
import com.google.auto.service.AutoService
import com.kronos.dokit.pthread.ui.PThreadHookUiActivity
import com.tencent.matrix.hook.HookManager.HookFailedException
import com.tencent.matrix.hook.pthread.PthreadHook
import com.tencent.matrix.hook.pthread.PthreadHook.ThreadStackShrinkConfig

/**
 *
 *  @Author LiABao
 *  @Since 2021/11/16
 *
 */
@AutoService(AbstractKit::class)
class PThreadKit : AbstractKit() {
    override val name: Int
        get() = R.string.kit_pthread_hook_kit
    override val icon: Int
        get() = R.mipmap.dk_ram


    override fun onClickWithReturn(activity: Activity): Boolean {
        activity.startActivity(Intent(activity, PThreadHookUiActivity::class.java))
        return super.onClickWithReturn(activity)
    }

    override fun onAppInit(context: Context?) {
        try {
            val config = ThreadStackShrinkConfig()
                .setEnabled(true)
                .addIgnoreCreatorSoPatterns(".*/app_tbs/.*")
                .addIgnoreCreatorSoPatterns(".*/libany\\.so$")
            PthreadHook.INSTANCE
                .addHookThread(".*")
                .setThreadStackShrinkConfig(config)
                .setThreadTraceEnabled(true)
                .enableQuicken(true)
            PthreadHook.INSTANCE.hook()
        } catch (e: HookFailedException) {
            e.printStackTrace()
        }
        AutoDumpListener()
    }

    override val isInnerKit: Boolean
        get() = true

    override fun innerKitId(): String {
        return "dokit_sdk_platform_thread_hook"
    }
}
