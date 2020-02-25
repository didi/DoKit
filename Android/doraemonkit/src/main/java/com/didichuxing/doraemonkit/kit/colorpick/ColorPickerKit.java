package com.didichuxing.doraemonkit.kit.colorpick;

import android.content.Context;
import android.content.Intent;

import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.config.ColorPickConfig;
import com.didichuxing.doraemonkit.constant.BundleKey;
import com.didichuxing.doraemonkit.constant.FragmentIndex;
import com.didichuxing.doraemonkit.kit.AbstractKit;
import com.didichuxing.doraemonkit.kit.Category;
import com.didichuxing.doraemonkit.ui.TranslucentActivity;

/**
 * Created by wanglikun on 2018/9/13.
 */

public class ColorPickerKit extends AbstractKit {
    private static final String TAG = "ColorPicker";

    @Override
    public int getCategory() {
        return Category.UI;
    }

    @Override
    public int getName() {
        return R.string.dk_kit_color_picker;
    }

    @Override
    public int getIcon() {
        return R.drawable.dk_color_picker;
    }

    @Override
    public void onClick(Context context) {

        Intent intent = new Intent(context, TranslucentActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(BundleKey.FRAGMENT_INDEX, FragmentIndex.FRAGMENT_COLOR_PICKER_SETTING);
        context.startActivity(intent);
    }

    @Override
    public void onAppInit(Context context) {
        ColorPickConfig.setColorPickOpen(context, false);
    }

    @Override
    public boolean isInnerKit() {
        return true;
    }

    @Override
    public String innerKitId() {
        return "dokit_sdk_ui_ck_color_pick";
    }
}