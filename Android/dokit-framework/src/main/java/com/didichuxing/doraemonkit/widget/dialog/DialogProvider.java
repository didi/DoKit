package com.didichuxing.doraemonkit.widget.dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

/**
 * Created by wanglikun on 2019/4/12
 */
public abstract class DialogProvider<T> {
    private T mData;
    private DialogFragment mHost;
    private View mView;
    private DialogListener mDialogListener;
    private boolean cancellable = true;

    public DialogProvider(T data, DialogListener listener) {
        this.mDialogListener = listener;
        this.mData = data;
    }

    public void setHost(DialogFragment host) {
        mHost = host;
    }

    public DialogFragment getHost() {
        return mHost;
    }

    public Context getContext() {
        if (mHost == null) {
            return null;
        }
        return mHost.getContext();
    }

    public abstract int getLayoutId();

    public final View createView(LayoutInflater inflater, ViewGroup parent) {
        mView = inflater.inflate(getLayoutId(), parent, false);
        return mView;
    }

    public final void onViewCreated(View view) {
        findViews(view);
        registerForListeners();
        bindData(mData);
    }


    protected void bindData(T data) {

    }

    protected abstract void findViews(View view);

    private void registerForListeners() {
        View positiveView = getPositiveView();
        if (positiveView != null) {
            positiveView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onPositive();
                }
            });
        }
        View negativeView = getNegativeView();
        if (negativeView != null) {
            negativeView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onNegative();
                }
            });
        }
        final View cancelView = getCancelView();
        if (cancelView != null) {
            cancelView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    cancel();
                }
            });
        }
    }

    private void onPositive() {
        boolean dismiss = true;
        if (mDialogListener != null) {
            dismiss = mDialogListener.onPositive(this);
        }
        if (dismiss) {
            dismiss();
        }
    }

    private void onNegative() {
        boolean dismiss = true;
        if (mDialogListener != null) {
            dismiss = mDialogListener.onNegative(this);
        }
        if (dismiss) {
            dismiss();
        }
    }

    public void show(FragmentManager childFragmentManager) {
        mHost.show(childFragmentManager, null);
    }

    public void dismiss() {
        mHost.dismiss();
    }

    protected void cancel() {
        dismiss();
        if (mDialogListener != null) {
            mDialogListener.onCancel(this);
        }
    }

    void onCancel() {
        if (mDialogListener != null) {
            mDialogListener.onCancel(this);
        }
    }

    public boolean isCancellable() {
        return cancellable;
    }

    public void setCancellable(boolean cancellable) {
        this.cancellable = cancellable;
    }

    protected View getPositiveView() {
        return null;
    }

    protected View getNegativeView() {
        return null;
    }

    protected View getCancelView() {
        return null;
    }

    public void setDialogListener(DialogListener dialogListener) {
        mDialogListener = dialogListener;
    }
}