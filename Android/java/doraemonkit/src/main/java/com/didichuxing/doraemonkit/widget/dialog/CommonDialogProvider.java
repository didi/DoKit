package com.didichuxing.doraemonkit.widget.dialog;

import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.didichuxing.doraemonkit.R;

/**
 * Created by wanglikun on 2019/4/12
 */
public class CommonDialogProvider<T extends DialogInfo> extends DialogProvider<T> {
    private TextView mPositive;
    private TextView mNegative;
    private TextView mTitle;
    private TextView mDesc;

    public CommonDialogProvider(T data, DialogListener listener) {
        super(data, listener);
    }

    @Override
    public int getLayoutId() {
        return R.layout.dk_dialog_common;
    }

    @Override
    protected void findViews(View view) {
        mPositive = view.findViewById(R.id.positive);
        mNegative = view.findViewById(R.id.negative);
        mTitle = view.findViewById(R.id.title);
        mDesc = view.findViewById(R.id.desc);
    }

    @Override
    protected void bindData(T data) {
        mTitle.setText(data.title);
        if (TextUtils.isEmpty(data.desc)) {
            mDesc.setVisibility(View.GONE);
        } else {
            mDesc.setVisibility(View.VISIBLE);
            mDesc.setText(data.desc);
        }
    }

    @Override
    protected View getPositiveView() {
        return mPositive;
    }

    @Override
    protected View getNegativeView() {
        return mNegative;
    }

    @Override
    public boolean isCancellable() {
        return false;
    }
}