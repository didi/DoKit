package com.didichuxing.doraemonkit.kit;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;

/**
 * Created by zhangweida on 2018/6/22.
 */

interface IKit {
    /**
     * 返回分类
     *
     * @return int
     */
    int getCategory();

    /**
     * 返回名称
     *
     * @return
     */
    @StringRes
    int getName();

    /**
     * 返回图标
     *
     * @return
     */
    @DrawableRes
    int getIcon();

    /**
     * 点击回调
     *
     * @param context
     */
    void onClick(Context context);

    /**
     * app 初始化时调用
     *
     * @param context
     */
    void onAppInit(Context context);

}