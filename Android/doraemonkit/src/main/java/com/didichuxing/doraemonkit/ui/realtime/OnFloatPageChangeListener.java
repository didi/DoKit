package com.didichuxing.doraemonkit.ui.realtime;

public interface OnFloatPageChangeListener {
        // 页面关闭时的回调，用以在关闭时重置页面checkbox状态
        void onFloatPageClose(String tag);
        // 打开的时候的回调
        void onFloatPageOpen(String tag);
    }