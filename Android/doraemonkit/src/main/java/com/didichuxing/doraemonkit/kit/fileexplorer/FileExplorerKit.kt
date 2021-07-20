package com.didichuxing.doraemonkit.kit.fileexplorer

import android.app.Activity
import android.content.Context
import com.didichuxing.doraemonkit.R
import com.didichuxing.doraemonkit.kit.AbstractKit
import com.google.auto.service.AutoService

/**
 * Created by zhangweida on 2018/6/26.
 */
@AutoService(AbstractKit::class)
class FileExplorerKit : AbstractKit() {
    override val name: Int
        get() = R.string.dk_kit_file_explorer
    override val icon: Int
        get() = R.mipmap.dk_file_explorer

    override fun onClickWithReturn(activity: Activity): Boolean {
        startUniversalActivity(FileExplorerFragment::class.java, activity, null, true)
        return true
    }

    override fun onAppInit(context: Context?) {}
    override val isInnerKit: Boolean
        get() = true

    override fun innerKitId(): String {
        return "dokit_sdk_comm_ck_sandbox"
    }
}