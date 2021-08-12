package com.didichuxing.doraemonkit.kit.dataclean

import android.app.Activity
import android.content.Context
import com.didichuxing.doraemonkit.R
import com.didichuxing.doraemonkit.kit.AbstractKit
import com.google.auto.service.AutoService

/**
 * Created by wanglikun on 2018/11/17.
 */
@AutoService(AbstractKit::class)
class DataCleanKit : AbstractKit() {
    override val name: Int
        get() = R.string.dk_kit_data_clean
    override val icon: Int
        get() = R.mipmap.dk_data_clean

    override fun onClickWithReturn(activity: Activity): Boolean {
        startUniversalActivity(DataCleanFragment::class.java, activity, null, true)
        return true
    }

    override fun onAppInit(context: Context?) {}
    override val isInnerKit: Boolean
        get() = true

    override fun innerKitId(): String {
        return "dokit_sdk_comm_ck_cache"
    }
}