package com.didichuxing.doraemonkit.kit.webdoor

import android.content.Context
import com.didichuxing.doraemonkit.R
import com.didichuxing.doraemonkit.constant.FragmentIndex
import com.didichuxing.doraemonkit.kit.AbstractKit

/**
 * Created by guofeng007 on 2020/6/8
 */
class WebDoorKit : AbstractKit() {
    override val name: Int
        get() = R.string.dk_kit_web_door

    override val icon: Int
        get() = R.mipmap.dk_web_door

    override fun onClick(context: Context?) {
        startUniversalActivity(context, FragmentIndex.FRAGMENT_WEB_DOOR)
    }

    override fun onAppInit(context: Context?) {}
    override val isInnerKit: Boolean
        get() = true

    override fun innerKitId(): String {
        return "dokit_sdk_comm_ck_h5"
    }
}