package com.didichuxing.doraemonkit.kit.colorpick;

import android.content.BroadcastReceiver;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;

import com.didichuxing.doraemonkit.datapick.DataPickManager;

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020-02-21-17:45
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class ColorPickManager {
    private MediaProjectionManager mMediaProjectionManager;
    private MediaProjection mMediaProjection;

    private ColorPickerDokitView mColorPickerDokitView;

    private static class Holder {
        private static ColorPickManager INSTANCE = new ColorPickManager();
    }

    public static ColorPickManager getInstance() {
        return ColorPickManager.Holder.INSTANCE;
    }


    MediaProjection getMediaProjection() {
        return mMediaProjection;
    }

    void setMediaProjection(MediaProjection mMediaProjection) {
        this.mMediaProjection = mMediaProjection;
    }

    public ColorPickerDokitView getColorPickerDokitView() {
        return mColorPickerDokitView;
    }

    public void setColorPickerDokitView(ColorPickerDokitView mColorPickerDokitView) {
        this.mColorPickerDokitView = mColorPickerDokitView;
    }
}
