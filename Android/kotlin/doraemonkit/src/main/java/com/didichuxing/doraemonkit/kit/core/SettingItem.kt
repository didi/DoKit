package com.didichuxing.doraemonkit.kit.core

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

/**
 * Created by wanglikun on 2018/9/14.
 */
data class SettingItem(@StringRes val desc: Int,
                       @DrawableRes val icon: Int,
                       val canCheck: Boolean = true,
                       var isChecked: Boolean = false,
                       var rightDesc: String? = null){

    constructor(desc: Int):this(desc
    ,0,false,false)

    constructor(desc: Int,isChecked: Boolean):this(desc
            ,0,true,isChecked)
}