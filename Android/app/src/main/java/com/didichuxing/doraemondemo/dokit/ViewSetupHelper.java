package com.didichuxing.doraemondemo.dokit;

import android.text.TextUtils;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

public final class ViewSetupHelper {
    private ViewSetupHelper() {
    }

    public static void setupButton(View container, int viewId, String title, View.OnClickListener onBtnClick) {
        TextView button = (TextView) container.findViewById(viewId);
        if (TextUtils.isEmpty(title)) {
            button.setVisibility(View.GONE);
        } else {
            button.setText(title);
            button.setVisibility(View.VISIBLE);
            button.setOnClickListener(onBtnClick);
        }
    }

    public static void setVisible(View container, int viewId, boolean visible) {
        container.findViewById(viewId).setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    public static void setupToggleButton(View container, int viewId, String title, boolean checked, CompoundButton.OnCheckedChangeListener onCheckedChangeListener) {
        ToggleButton toggleButton = (ToggleButton) container.findViewById(viewId);
        if (TextUtils.isEmpty(title)) {
            toggleButton.setVisibility(View.GONE);
        } else {
            toggleButton.setVisibility(View.VISIBLE);
            toggleButton.setTextOn(title + " ON");
            toggleButton.setTextOff(title + " OFF");
            toggleButton.setChecked(checked);
            toggleButton.setOnCheckedChangeListener(onCheckedChangeListener);
            onCheckedChangeListener.onCheckedChanged(toggleButton, checked);
        }
    }
}
