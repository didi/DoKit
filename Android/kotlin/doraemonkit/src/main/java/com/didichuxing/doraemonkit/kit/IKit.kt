package com.didichuxing.doraemonkit.kit

import android.content.Context
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

/**
 * Created by zhangweida on 2018/6/22.
 * 工具入口 请继承AbstractKit
 */
internal interface IKit {

    /**
     * 返回名称
     *
     * @return
     */
    @get:StringRes
    val name: Int

    /**
     * 返回图标
     *
     * @return
     */
    @get:DrawableRes
    val icon: Int

    /**
     * 点击回调
     *
     * @param context
     */
    fun onClick(context: Context?)

    /**
     * app 初始化时调用
     *
     * @param context
     */
    fun onAppInit(context: Context?)
}