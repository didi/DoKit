package com.didichuxing.doraemonkit.kit.dbdebug

import android.app.Activity
import android.content.Context
import com.didichuxing.doraemonkit.R
import com.didichuxing.doraemonkit.kit.AbstractKit

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2019-09-24-17:05
 * 描    述：数据库远程访问入口 去掉
 * 修订历史：
 * ================================================
 */
//@AutoService(AbstractKit.class)
class DbDebugKit : AbstractKit() {
    override val name: Int
        get() = R.string.dk_tools_dbdebug
    override val icon: Int
        get() = R.mipmap.dk_db_view

    override fun onClickWithReturn(activity: Activity): Boolean {
        return true
    }

    override fun onAppInit(context: Context?) {}
    override val isInnerKit: Boolean
        get() = true

    override fun innerKitId(): String {
        return "dokit_sdk_comm_ck_dbview"
    }
}