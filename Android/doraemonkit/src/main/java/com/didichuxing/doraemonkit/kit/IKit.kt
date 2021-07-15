package com.didichuxing.doraemonkit.kit

import android.app.Activity
import android.content.Context
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

/**
 * Created by zhangweida on 2018/6/22.
 * 工具入口 请继承AbstractKit
 */
internal interface IKit {
    /**
     * 返回分类
     *
     * @return int
     */
    val category: Int

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
    @Deprecated("请使用onClickWithReturn代替")
    fun onClick(context: Context?) {
    }

    /**
     * 点击回调 带返回值
     * @return true 隐藏面板 false 不隐藏面板
     */
    fun onClickWithReturn(activity: Activity): Boolean {
        return true
    }

    /**
     * app 初始化时调用
     *
     * @param context
     */
    fun onAppInit(context: Context?)
}