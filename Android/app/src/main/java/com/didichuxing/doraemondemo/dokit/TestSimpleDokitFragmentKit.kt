package com.didichuxing.doraemondemo.dokit

import android.app.Activity
import android.content.Context
import android.os.Bundle
import com.didichuxing.doraemondemo.R
import com.didichuxing.doraemonkit.DoKit
import com.didichuxing.doraemonkit.kit.AbstractKit
import com.didichuxing.doraemonkit.kit.Category

/**
 * @Author: changzuozhen
 * @Date: 2020-12-22
 */
class TestSimpleDokitFragmentKit : AbstractKit() {
    override val category: Int
        get() = Category.BIZ
    override val name: Int
        get() = R.string.dk_kit_fullscreen
    override val icon: Int
        get() = R.mipmap.dk_sys_info

    override fun onClickWithReturn(activity: Activity): Boolean {
        val bundle = Bundle()
        bundle.putString("test", "test")
        DoKit.launchFullScreen(CustomDokitFragment::class, activity, bundle)
        return true
    }

    override fun onAppInit(context: Context?) {
    }
}