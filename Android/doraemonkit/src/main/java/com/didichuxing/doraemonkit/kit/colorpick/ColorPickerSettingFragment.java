package com.didichuxing.doraemonkit.kit.colorpick;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.projection.MediaProjectionManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.config.ColorPickConfig;
import com.didichuxing.doraemonkit.constant.PageTag;
import com.didichuxing.doraemonkit.constant.RequestCode;
import com.didichuxing.doraemonkit.ui.base.BaseFragment;
import com.didichuxing.doraemonkit.ui.base.FloatPageManager;
import com.didichuxing.doraemonkit.ui.base.PageIntent;

/**
 * Created by wanglikun on 2018/9/15.
 */

public class ColorPickerSettingFragment extends BaseFragment {


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        requestCaptureScreen();
        ColorPickConfig.setColorPickOpen(getContext(), true);
    }

    private boolean requestCaptureScreen() {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.LOLLIPOP) {
            return false;
        }
        MediaProjectionManager mediaProjectionManager = (MediaProjectionManager) getContext().getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        if (mediaProjectionManager == null) {
            return false;
        }
        startActivityForResult(mediaProjectionManager.createScreenCaptureIntent(), RequestCode.CAPTURE_SCREEN);
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RequestCode.CAPTURE_SCREEN && resultCode == Activity.RESULT_OK) {
            showColorPicker(data);
            finish();
        } else {
            showToast("start color pick fail");
            finish();
        }
    }

    private void showColorPicker(Intent data) {
        PageIntent pageIntent = new PageIntent(ColorPickerInfoFloatPage.class);
        pageIntent.tag = PageTag.PAGE_COLOR_PICKER_INFO;
        pageIntent.mode = PageIntent.MODE_SINGLE_INSTANCE;
        FloatPageManager.getInstance().add(pageIntent);

        pageIntent = new PageIntent(ColorPickerFloatPage.class);
        pageIntent.bundle = data.getExtras();
        pageIntent.mode = PageIntent.MODE_SINGLE_INSTANCE;
        FloatPageManager.getInstance().add(pageIntent);
    }

    @Override
    protected int onRequestLayout() {
        return R.layout.dk_fragment_color_picker_setting;
    }
}
