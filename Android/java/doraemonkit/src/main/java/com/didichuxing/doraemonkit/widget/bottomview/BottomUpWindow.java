package com.didichuxing.doraemonkit.widget.bottomview;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.PopupWindow;

import com.didichuxing.doraemonkit.R;

/**
 * 从底部向上弹出的选择器
 *
 * @author vinda
 * @since 15/5/21
 */
public class BottomUpWindow extends PopupWindow {
    private final String TAG = "BottomUpSelectWindow";
    private View thisView;
    private View tv_submit;
    private final View titleViiew;
    private FrameLayout contentPanel;
    private AssociationView associationView;

    private View ll_panel;
    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final int vid = v.getId();
            if (vid == R.id.tv_submit) {
                Object submit = associationView.submit();
                if (mOnSubmitListener != null) {
                    mOnSubmitListener.submit(submit);
                }
                dismiss();
            } else if (vid == R.id.tv_cancel) {
                cancel();
            }
        }

    };

    public BottomUpWindow(Context context) {
        super(context);
        LayoutInflater layoutInflater = LayoutInflater.from(context);

        thisView = layoutInflater.inflate(R.layout.dk_item_layout_bottom_up_select_window, null);
        ll_panel = thisView.findViewById(R.id.ll_panel);
        titleViiew = thisView.findViewById(R.id.tv_title);

        contentPanel = thisView.findViewById(R.id.content);
        this.setContentView(thisView);
        initView();

        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setFocusable(true);
        this.setTouchable(true);
        this.setOutsideTouchable(true);

        ColorDrawable dw = new ColorDrawable(0x80000000);

        this.setBackgroundDrawable(dw);
    }

    private void initView() {
        tv_submit = thisView.findViewById(R.id.tv_submit);
        tv_submit.setOnClickListener(onClickListener);
        thisView.findViewById(R.id.tv_cancel).setOnClickListener(onClickListener);
        //点击在上方时关闭
        thisView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel();

            }
        });
    }

    /**
     * 设置中间内容的view
     *
     * @param view
     */
    public BottomUpWindow setContent(AssociationView view) {
        associationView = view;
        contentPanel.removeAllViews();
        contentPanel.addView(associationView.getView());
        associationView.setOnStateChangeListener(new AssociationView.OnStateChangeListener() {
            @Override
            public void onStateChanged() {
                tv_submit.setEnabled(associationView.isCanSubmit());
            }
        });
        return this;
    }

    @Override
    public void dismiss() {
        //动画
        TranslateAnimation animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 1);
        animation.setDuration(200);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                ll_panel.setVisibility(View.GONE);
                dismissWindow();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        ll_panel.startAnimation(animation);
        if (associationView != null) {
            associationView.onHide();
        }
    }

    /**
     * 隐藏整个窗口
     */
    private void dismissWindow() {
        try {
            super.dismiss();
        } catch (Throwable e) {
        }
    }

    private void cancel() {
        associationView.cancel();
        dismiss();

        if (mOnSubmitListener != null) {
            mOnSubmitListener.cancel();
        }
    }

    public BottomUpWindow show(View parent) {
        this.showAtLocation(parent, Gravity.BOTTOM
                | Gravity.CENTER_HORIZONTAL, 0, 0);
        ll_panel.setVisibility(View.VISIBLE);
        //动画
        TranslateAnimation animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, 1, Animation.RELATIVE_TO_SELF, 0);
        animation.setDuration(200);
        ll_panel.startAnimation(animation);
        if (associationView != null) {
            associationView.onShow();
        }
        return this;
    }

    private OnSubmitListener mOnSubmitListener;

    public void setOnSubmitListener(OnSubmitListener onSubmitListener) {
        this.mOnSubmitListener = onSubmitListener;
    }

    public interface OnSubmitListener {

        void submit(Object object);

        void cancel();
    }
}
