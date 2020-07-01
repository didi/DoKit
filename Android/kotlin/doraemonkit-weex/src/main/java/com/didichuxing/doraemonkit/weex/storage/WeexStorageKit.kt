package com.didichuxing.doraemonkit.weex.storage

import android.content.Context
import com.didichuxing.doraemonkit.kit.AbstractKit
import com.didichuxing.doraemonkit.weex.R
import com.didichuxing.doraemonkit.weex.common.DKCommonActivity.Companion.startWith
import com.didichuxing.doraemonkit.weex.storage.fragment.StorageFragment

/**
 * Created by alvince on 2020/7/1
 *
 * @author alvince.zy@gmail.com
 */
class WeexStorageKit : AbstractKit() {

    override val name: Int
        get() = R.string.dk_storage_cache_name

    override val icon: Int
        get() = R.mipmap.dk_file_explorer

    override val isInnerKit: Boolean
        get() = true

    override fun onAppInit(context: Context?) {
    }

    override fun onClick(context: Context?) {
        context?.also {
            startWith(it, StorageFragment::class.java)
        }
    }

    override fun innerKitId(): String = "dokit_sdk_weex_ck_storage"

}