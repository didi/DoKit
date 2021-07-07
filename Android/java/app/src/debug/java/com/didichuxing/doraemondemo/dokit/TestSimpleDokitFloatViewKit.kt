package com.didichuxing.doraemondemo.dokit

import android.content.Context
import com.didichuxing.doraemondemo.R
import com.didichuxing.doraemonkit.kit.AbstractKit
import com.didichuxing.doraemonkit.kit.Category
import com.didichuxing.doraemonkit.kit.core.SimpleDokitStarter

/**
 * @Author: changzuozhen
 * @Date: 2020-12-22
 */
class TestSimpleDokitFloatViewKit : AbstractKit() {
    override val category: Int
        get() = Category.BIZ
    override val name: Int
        get() = R.string.dk_kit_simple_float
    override val icon: Int
        get() = R.mipmap.dk_sys_info

    override fun onClickWithReturn(context: Context?): Boolean {
        SimpleDokitStarter.startFloating(TestSimpleDokitFloatView::class.java)
        return true
    }

    override fun onAppInit(context: Context?) {
    }
}