package com.didichuxing.doraemonkit.kit.colorpick;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.projection.MediaProjectionManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.view.View;

import com.didichuxing.doraemonkit.R;
import com.didichuxing.doraemonkit.config.ColorPickConfig;
import com.didichuxing.doraemonkit.constant.DoKitConstant;
import com.didichuxing.doraemonkit.constant.RequestCode;
import com.didichuxing.doraemonkit.kit.core.BaseFragment;
import com.didichuxing.doraemonkit.kit.core.DokitIntent;
import com.didichuxing.doraemonkit.kit.core.DokitViewManager;
import com.didichuxing.doraemonkit.kit.core.SimpleDokitStarter;
import com.didichuxing.doraemonkit.util.ToastUtils;

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
            if (!DoKitConstant.IS_NORMAL_FLOAT_MODE) {
                finish();
            }
            showColorPicker(data);
        } else {
            ToastUtils.showShort("start color pick fail");
            finish();
        }
    }

    /**
     * 显示颜色拾取器
     *
     * @param intent
     */
    private void showColorPicker(Intent intent) {
        DokitViewManager.getInstance().detachToolPanel();

        SimpleDokitStarter.startFloating(ColorPickerInfoDokitView.class);


        Bundle bundle = new Bundle();
        bundle.putParcelable("data", intent);
        SimpleDokitStarter.startFloating(ColorPickerDokitView.class, bundle);

    }

    @Override
    protected int onRequestLayout() {
        return R.layout.dk_fragment_color_picker_setting;
    }
}
