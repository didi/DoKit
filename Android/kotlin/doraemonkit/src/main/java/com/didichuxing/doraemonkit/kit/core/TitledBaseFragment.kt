package com.didichuxing.doraemonkit.kit.core

import com.didichuxing.doraemonkit.widget.titlebar.TitleBar

/**
 * @author wanglikun
 * @date 2018/10/26
 */
abstract class TitledBaseFragment : BaseFragment(), TitleBar.OnTitleBarClickListener {
    override fun onLeftClick() {
        finish()
    }

    override fun onRightClick() {}
}