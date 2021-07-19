package com.didichuxing.doraemonkit.kit.core;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;

/**
 * @Author: changzuozhen
 * @Date: 2020-12-22
 * <p>
 * 悬浮窗，支持折叠
 * @see SimpleDokitView
 * 启动工具函数
 */
public abstract class SimpleDokitView extends AbsDokitView {


    @Override
    public void onEnterForeground() {
        super.onEnterForeground();
    }

    @Override
    public void onEnterBackground() {
        super.onEnterBackground();
    }

    public void showContainer(boolean isChecked) {
    }

    @Override
    public void onCreate(Context context) {

    }


    @Override
    public View onCreateView(Context context, FrameLayout rootView) {
        return null;
    }

    @Override
    public void onViewCreated(FrameLayout rootView) {

    }

    protected abstract int getLayoutId();


    @Override
    public void initDokitViewLayoutParams(DokitViewLayoutParams params) {

    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    @Override
    public boolean shouldDealBackKey() {
        return true;
    }

    protected void initView() {
    }

    @Override
    public void onPause() {

    }
}