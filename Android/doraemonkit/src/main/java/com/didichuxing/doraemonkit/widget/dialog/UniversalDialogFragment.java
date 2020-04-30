package com.didichuxing.doraemonkit.widget.dialog;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.didichuxing.doraemonkit.R;

/**
 * Created by wanglikun on 2019/4/12
 */
public class UniversalDialogFragment extends DialogFragment {

    private DialogProvider mProvider;

    public void setProvider(DialogProvider provider) {
        mProvider = provider;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(androidx.fragment.app.DialogFragment.STYLE_NO_TITLE, 0);
    }


    @Override
    public int getTheme() {
        return R.style.DK_Dialog;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (mProvider != null) {
            setCancelable(mProvider.isCancellable());
        }

        Window window = getDialog().getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.gravity = getGravity();
        lp.width = getWidth();
        lp.height = getHeight();
        window.setAttributes(lp);

        return mProvider.createView(inflater, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mProvider.onViewCreated(view);
    }

    @Override
    public void onStart() {
        super.onStart();
        getDialog().getWindow().setLayout(getWidth(), getHeight());
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(android.R.color.transparent)));
    }

    @Override
    public void onCancel(android.content.DialogInterface dialog) {
        super.onCancel(dialog);
        mProvider.onCancel();
    }

    protected int getGravity() {
        return Gravity.CENTER;
    }

    protected int getWidth() {
        return WindowManager.LayoutParams.WRAP_CONTENT;
    }

    protected int getHeight() {
        return WindowManager.LayoutParams.WRAP_CONTENT;
    }
}
