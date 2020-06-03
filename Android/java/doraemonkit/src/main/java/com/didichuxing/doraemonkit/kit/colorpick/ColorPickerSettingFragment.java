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
import com.didichuxing.doraemonkit.constant.DokitConstant;
import com.didichuxing.doraemonkit.constant.RequestCode;
import com.didichuxing.doraemonkit.kit.core.BaseFragment;
import com.didichuxing.doraemonkit.kit.core.DokitIntent;
import com.didichuxing.doraemonkit.kit.core.DokitViewManager;

/**
 * @author wanglikun
 * @date 2018/9/15
 * 屏幕取色器fragment
 */

public class ColorPickerSettingFragment extends BaseFragment {


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (requestCaptureScreen()) {
            ColorPickConfig.setColorPickOpen(true);
        }
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
            if (!DokitConstant.IS_NORMAL_FLOAT_MODE) {
                finish();
            }
            showColorPicker(data);
        } else {
            showToast("start color pick fail");
            finish();
        }
    }

    /**
     * 显示颜色拾取器
     *
     * @param data
     */
    private void showColorPicker(Intent data) {
        DokitViewManager.getInstance().detachToolPanel();

        DokitIntent pageIntent = new DokitIntent(ColorPickerInfoDokitView.class);
        pageIntent.mode = DokitIntent.MODE_SINGLE_INSTANCE;
        DokitViewManager.getInstance().attach(pageIntent);

        pageIntent = new DokitIntent(ColorPickerDokitView.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable("data", data);
        pageIntent.bundle = bundle;
        pageIntent.mode = DokitIntent.MODE_SINGLE_INSTANCE;
        DokitViewManager.getInstance().attach(pageIntent);

    }

    @Override
    protected int onRequestLayout() {
        return R.layout.dk_fragment_color_picker_setting;
    }
}
