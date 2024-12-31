package com.didichuxing.doraemonkit.widget.bottomview;

import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentTransaction;

import com.didichuxing.doraemonkit.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

/**
 * 从底部向上弹出的选择器
 *
 * @author vinda
 * @since 15/5/21
 */
public class BottomUpWindow extends BottomSheetDialogFragment {
    private final String TAG = "BottomUpSelectWindow";
    private View thisView;
    private View tv_submit;
    private View titleViiew;
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


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        thisView = inflater.inflate(R.layout.dk_item_layout_bottom_up_select_window, container,false);
        return thisView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ll_panel = thisView.findViewById(R.id.ll_panel);
        titleViiew = thisView.findViewById(R.id.tv_title);

        contentPanel = thisView.findViewById(R.id.content);
        initView();

        thisView.setFocusable(true);

        ColorDrawable dw = new ColorDrawable(0x80000000);

        thisView.setBackgroundDrawable(dw);
        setUpContent();
    }

    private void setUpContent() {
        contentPanel.removeAllViews();
        contentPanel.addView(associationView.getView());
        associationView.setOnStateChangeListener(new AssociationView.OnStateChangeListener() {
            @Override
            public void onStateChanged() {
                tv_submit.setEnabled(associationView.isCanSubmit());
            }
        });
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
        return this;
    }

    @Override
    public void dismiss() {
        if (associationView != null) {
            associationView.onHide();
        }
        super.dismiss();
    }



    private void cancel() {
        if (associationView!=null) {
            associationView.cancel();
        }

        dismiss();

        if (mOnSubmitListener != null) {
            mOnSubmitListener.cancel();
        }
    }

    @Override
    public int show(@NonNull FragmentTransaction transaction, @Nullable String tag) {
        if (associationView != null) {
            associationView.onShow();
        }
        return super.show(transaction, tag);

    }


    private OnSubmitListener mOnSubmitListener;

    public BottomUpWindow setOnSubmitListener(OnSubmitListener onSubmitListener) {
        this.mOnSubmitListener = onSubmitListener;
        return this;
    }

    public interface OnSubmitListener {

        void submit(Object object);

        void cancel();
    }
}
