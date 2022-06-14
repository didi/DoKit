package com.didichuxing.doraemonkit.kit.permissionlist

import android.app.Activity
import android.content.Context
import com.didichuxing.doraemonkit.R
import com.didichuxing.doraemonkit.kit.AbstractKit
import com.didichuxing.doraemonkit.kit.network.ui.NetWorkMonitorFragment
import com.google.auto.service.AutoService

/**
 * Created by wanglikun on 2019/1/7
 */
@AutoService(AbstractKit::class)
class PermissionListKit : AbstractKit() {
    override val name: Int
        get() = R.string.dk_kit_permission_list
    override val icon: Int
        get() = R.mipmap.dk_permission_list

    override fun onClickWithReturn(activity: Activity): Boolean {
        ExpandableActivity.openActivity(activity)
        return true
    }

    override fun onAppInit(context: Context?) {
    }

    override val isInnerKit: Boolean
        get() = true

    override fun innerKitId(): String = "dokit_sdk_platform_ck_permission"
}
