package com.didichuxing.doraemonkit.kit.core

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.LayoutRes
import com.didichuxing.doraemonkit.R
import com.didichuxing.doraemonkit.widget.titlebar.HomeTitleBar

/**
 * @Author: changzuozhen
 * @Date: 2020-12-22
 *
 *
 * 全屏页面
 * @see com.didichuxing.doraemonkit.kit.core.AbsDoKitFragment
 * 启动工具函数
 *
 * @see com.didichuxing.doraemonkit.kit.core.SimpleDoKitStarter.startFullScreen
 */
abstract class AbsDoKitFragment : BaseFragment() {
    val bundle: Bundle?
        get() = if (activity == null || requireActivity().intent == null || requireActivity().intent.extras == null) {
            null
        } else requireActivity().intent.extras

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = super.onCreateView(inflater, container, savedInstanceState)
        inflater.inflate(
            layoutId(),
            rootView!!.findViewById<View>(R.id.contentContainer) as FrameLayout,
            true
        )
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        onViewCreated(view)
    }

    override fun onRequestLayout(): Int {
        return R.layout.dk_fragment_simple_dokit_page
    }

    protected open fun onViewCreated(view: View?) {}

    @LayoutRes
    abstract fun layoutId(): Int

   open fun initTitle(): String {
        return this.javaClass.simpleName
    }

    private fun initView() {
        val homeTitleBar = findViewById<HomeTitleBar>(R.id.title_bar)
        homeTitleBar.setTitle(initTitle())
        homeTitleBar.setListener { requireActivity().finish() }
    }


}
