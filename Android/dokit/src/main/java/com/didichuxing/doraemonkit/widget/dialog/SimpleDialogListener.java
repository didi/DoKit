package com.didichuxing.doraemonkit.widget.dialog;

import org.jetbrains.annotations.NotNull;

/**
 * Created by wanglikun on 2019/4/12
 */
public class SimpleDialogListener implements DialogListener {
    @Override
    public boolean onPositive(@NotNull DialogProvider<?> dialogProvider) {
        return false;
    }

    @Override
    public boolean onNegative(@NotNull DialogProvider<?> dialogProvider) {
        return false;
    }

    @Override
    public void onCancel(@NotNull DialogProvider<?> dialogProvider) {

    }

}