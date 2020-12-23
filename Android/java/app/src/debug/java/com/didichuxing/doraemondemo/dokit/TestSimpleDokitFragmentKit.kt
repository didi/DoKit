package com.didichuxing.doraemondemo.dokit

import android.content.Context
import android.os.Bundle
import com.didichuxing.doraemondemo.R
import com.didichuxing.doraemonkit.kit.AbstractKit
import com.didichuxing.doraemonkit.kit.Category
import com.didichuxing.doraemonkit.kit.core.SimpleDokitStarter

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

    override fun onClick(context: Context?) {
        val bundle = Bundle()
        bundle.putString("test", "test")
        SimpleDokitStarter.startFullScreen(TestSimpleDokitFragment::class.java, context)
    }

    override fun onAppInit(context: Context?) {
    }
}