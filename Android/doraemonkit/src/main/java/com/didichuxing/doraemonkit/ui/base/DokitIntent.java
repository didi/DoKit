package com.didichuxing.doraemonkit.ui.base;

import android.app.Activity;
import android.os.Bundle;

import com.blankj.utilcode.util.ActivityUtils;

/**
 * Created by jintai on 2019/9/16.
 * dokitView intent
 */

public class DokitIntent {

    /**
     * 全局的悬浮窗
     */
    public static final int MODE_SINGLE_INSTANCE = 1;

    /**
     * 只在页面创建时显示 页面resume时不恢复
     */
    public static final int MODE_ONCE = 2;

    public Class<? extends AbsDokitView> targetClass;

    public Bundle bundle;
    /**
     * 赋值为类名 不接受对外赋值
     */
    private String tag;

    public String getTag() {
        return tag;
    }

    public Activity activity;

    public int mode = MODE_SINGLE_INSTANCE;


    public DokitIntent(Class<? extends AbsDokitView> targetClass) {
        this.activity = ActivityUtils.getTopActivity();
        this.targetClass = targetClass;
        tag = targetClass.getSimpleName();
    }

}
