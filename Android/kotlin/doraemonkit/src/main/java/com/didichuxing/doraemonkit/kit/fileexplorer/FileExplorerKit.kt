package com.didichuxing.doraemonkit.kit.fileexplorer

import android.content.Context
import com.didichuxing.doraemonkit.R
import com.didichuxing.doraemonkit.constant.FragmentIndex
import com.didichuxing.doraemonkit.kit.AbstractKit

/**
 * Created by zhangweida on 2018/6/26.
 */
class FileExplorerKit : AbstractKit() {
    override val name: Int
        get() = R.string.dk_kit_file_explorer

    override val icon: Int
        get() = R.mipmap.dk_file_explorer

    override fun onClick(context: Context?) {
        startUniversalActivity(FragmentIndex.FRAGMENT_FILE_EXPLORER)
    }

    override fun onAppInit(context: Context?) {}
    override val isInnerKit: Boolean
        get() = true

    override fun innerKitId(): String {
        return "dokit_sdk_comm_ck_sandbox"
    }
}