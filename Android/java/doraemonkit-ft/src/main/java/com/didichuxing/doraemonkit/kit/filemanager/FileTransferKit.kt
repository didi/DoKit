package com.didichuxing.doraemonkit.kit.filemanager

import android.content.Context
import com.didichuxing.doraemonkit.R
import com.didichuxing.doraemonkit.constant.FragmentIndex
import com.didichuxing.doraemonkit.kit.AbstractKit
import com.google.auto.service.AutoService

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020/6/23-13:30
 * 描    述：
 * 修订历史：
 * ================================================
 */
@AutoService(AbstractKit::class)
class FileTransferKit : AbstractKit() {
    override val name: Int
        get() = R.string.dk_kit_file_transfer
    override val icon: Int
        get() = R.mipmap.dk_icon_file_manager

    override fun onClickWithReturn(context: Context?): Boolean {
        startUniversalActivity(FileTransferFragment::class.java, context)
        return true
    }

    override fun onAppInit(context: Context?) {
    }

    override val isInnerKit: Boolean
        get() = true

    override fun innerKitId(): String {
        return "dokit_sdk_platform_ck_filetransfer"
    }
}