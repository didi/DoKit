package com.didichuxing.doraemonkit.kit.filemanager

import android.content.Context
import com.didichuxing.doraemonkit.R
import com.didichuxing.doraemonkit.constant.FragmentIndex
import com.didichuxing.doraemonkit.kit.AbstractKit

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020/6/23-13:30
 * 描    述：
 * 修订历史：
 * ================================================
 */
class FileTransferKit : AbstractKit() {
    override val name: Int
        get() = R.string.dk_kit_file_transfer
    override val icon: Int
        get() = R.mipmap.dk_health

    override fun onClick(context: Context?) {
        context?.let {
            startUniversalActivity(it, FragmentIndex.FRAGMENT_FILE_TRANSFER)
        }
    }

    override fun onAppInit(context: Context?) {
    }
}