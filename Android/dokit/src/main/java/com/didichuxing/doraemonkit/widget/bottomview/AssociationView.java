package com.didichuxing.doraemonkit.widget.bottomview;

import android.view.View;

/**
 * 可提交的view
 *
 * @author vinda
 * @since 15/5/21
 */
public abstract class AssociationView {

    private OnStateChangeListener onStateChangeListener;

    /**
     * 提交
     */
    public abstract Object submit();

    /**
     * 取消
     */
    public abstract void cancel();

    /**
     * 获取视图
     *
     * @return
     */
    public abstract View getView();

    /**
     * 能否提交
     *
     * @return
     */
    public abstract boolean isCanSubmit();

    public abstract void onShow();

    public abstract void onHide();


    final void setOnStateChangeListener(OnStateChangeListener listener) {
        onStateChangeListener = listener;
    }

    final OnStateChangeListener getOnStateChangeListener() {
        return onStateChangeListener;
    }


    interface OnStateChangeListener {
        void onStateChanged();
    }
}
