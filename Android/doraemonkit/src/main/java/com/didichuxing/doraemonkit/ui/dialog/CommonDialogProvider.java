package com.didichuxing.doraemonkit.ui.dialog;

import android.view.View;
import android.widget.TextView;

import com.didichuxing.doraemonkit.R;

/**
 * Created by wanglikun on 2019/4/12
 */
public class CommonDialogProvider extends DialogProvider<DialogInfo> {
    private TextView mPositive;
    private TextView mNegative;
    private TextView mTitle;
    private TextView mDesc;

    public CommonDialogProvider(DialogInfo data, DialogListener listener) {
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
    protected void bindData(DialogInfo data) {
        mTitle.setText(data.title);
        mDesc.setText(data.desc);
    }

    @Override
    protected View getPositiveView() {
        return mPositive;
    }

    @Override
    protected View getNegativeView() {
        return mNegative;
    }
}