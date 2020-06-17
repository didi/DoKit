package com.didichuxing.doraemonkit.kit.dataclean

import android.content.Context
import com.didichuxing.doraemonkit.R
import com.didichuxing.doraemonkit.constant.FragmentIndex
import com.didichuxing.doraemonkit.kit.AbstractKit

/**
 * Created by wanglikun on 2018/11/17.
 */
class DataCleanKit : AbstractKit() {
    override val name: Int
        get() = R.string.dk_kit_data_clean

    override val icon: Int
        get() = R.mipmap.dk_data_clean

    override fun onClick(context: Context?) {
        startUniversalActivity(context, FragmentIndex.FRAGMENT_DATA_CLEAN)
    }

    override fun onAppInit(context: Context?) {}
    override val isInnerKit: Boolean
        get() = true

    override fun innerKitId(): String {
        return "dokit_sdk_comm_ck_cache"
    }
}