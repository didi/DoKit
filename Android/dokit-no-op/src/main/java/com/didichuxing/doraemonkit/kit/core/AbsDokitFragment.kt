package com.didichuxing.doraemonkit.kit.core

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes

/**
 * @Author: changzuozhen
 * @Date: 2020-12-22
 *
 *
 * 全屏页面
 * @see com.didichuxing.doraemonkit.kit.core.AbsDokitFragment
 * 启动工具函数
 *
 * @see com.didichuxing.doraemonkit.kit.core.SimpleDoKitStarter.startFullScreen
 */
abstract class AbsDokitFragment : BaseFragment() {
    val bundle: Bundle?
        get() = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onRequestLayout(): Int {
        return -1
    }

    protected open fun onViewCreated(view: View?) {}

    @LayoutRes
    abstract fun layoutId(): Int

    open fun initTitle(): String {
        return ""
    }


}