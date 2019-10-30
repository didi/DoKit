package com.didichuxing.doraemonkit.ui.base;

import android.os.Bundle;

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2019-09-29-17:39
 * 描    述：关于全局dokitView的基本信息 由于普通的浮标是每个页面自己管理的
 * 需要有一个map用来保存当前每个类型的dokitview 便于新开页面和页面resume时的dokitview添加
 * 修订历史：
 * ================================================
 */
class GlobalSingleDokitViewInfo {
    Class<? extends AbsDokitView> mAbsDokitViewClass;
    String mTag;
    int mMode;
    Bundle mBundle;

    GlobalSingleDokitViewInfo(Class<? extends AbsDokitView> absDokitViewClass, String tag, int mode, Bundle bundle) {
        this.mAbsDokitViewClass = absDokitViewClass;
        this.mTag = tag;
        this.mMode = mode;
        this.mBundle = bundle;
    }

    Class<? extends AbsDokitView> getAbsDokitViewClass() {
        return mAbsDokitViewClass;
    }


    public String getTag() {
        return mTag;
    }


    public int getMode() {
        return mMode;
    }


    public Bundle getBundle() {
        return mBundle;
    }


    @Override
    public String toString() {
        return "GlobalSingleDokitViewInfo{" +
                "absDokitViewClass=" + mAbsDokitViewClass +
                ", tag='" + mTag + '\'' +
                ", mode=" + mMode +
                ", bundle=" + mBundle +
                '}';
    }
}
