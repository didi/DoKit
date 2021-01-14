package com.didichuxing.doraemonkit.kit.core;


import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;

/**
 * Created by wanglikun on 2018/9/14.
 */

public class SettingItem {
    public @StringRes
    final int desc;

    public String rightDesc;

    public @DrawableRes
    int icon;

    public boolean isChecked;

    public boolean canCheck;

    public SettingItem(@StringRes int desc) {
        this.desc = desc;
    }

    public SettingItem(@StringRes int desc, boolean isChecked) {
        this.desc = desc;
        this.isChecked = isChecked;
        this.canCheck = true;
    }

    public SettingItem(@StringRes int desc, @DrawableRes int icon) {
        this.desc = desc;
        this.icon = icon;
    }
}