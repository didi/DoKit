package com.didichuxing.doraemonkit.kit.core

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

/**
 * Created by wanglikun on 2018/9/14.
 */
class SettingItem {
    @JvmField
    @StringRes
    val desc: Int
    @JvmField
    var rightDesc: String? = null

    @JvmField
    @DrawableRes
    var icon = 0
    @JvmField
    var isChecked = false
    @JvmField
    var canCheck = false

    constructor(@StringRes desc: Int) {
        this.desc = desc
    }

    constructor(@StringRes desc: Int, isChecked: Boolean) {
        this.desc = desc
        this.isChecked = isChecked
        canCheck = true
    }

    constructor(@StringRes desc: Int, @DrawableRes icon: Int) {
        this.desc = desc
        this.icon = icon
    }
}