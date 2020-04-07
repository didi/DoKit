package com.didichuxing.doraemonkit.aop.bigimg.imageloader;

import android.graphics.Bitmap;
import androidx.annotation.Nullable;
import android.view.View;

import com.didichuxing.doraemonkit.kit.largepicture.LargePictureManager;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020/3/23-15:25
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class DokitImageLoadingListener implements ImageLoadingListener {
    private static final String TAG = "DokitImageLoadingListener";
    /**
     * 原始的ImageLoadingListener
     */
    @Nullable
    private ImageLoadingListener mOriginalImageLoadingListener;

    public DokitImageLoadingListener(ImageLoadingListener imageLoadingListener) {
        this.mOriginalImageLoadingListener = imageLoadingListener;
    }

    @Override
    public void onLoadingStarted(String imageUri, View view) {
        if (mOriginalImageLoadingListener != null) {
            mOriginalImageLoadingListener.onLoadingStarted(imageUri, view);
        }
    }

    @Override
    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
        if (mOriginalImageLoadingListener != null) {
            mOriginalImageLoadingListener.onLoadingFailed(imageUri, view, failReason);
        }
    }

    @Override
    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
        LargePictureManager.getInstance().transform(imageUri, loadedImage, false, "ImageLoader");
        if (mOriginalImageLoadingListener != null) {
            mOriginalImageLoadingListener.onLoadingComplete(imageUri, view, loadedImage);
        }
    }

    @Override
    public void onLoadingCancelled(String imageUri, View view) {
        if (mOriginalImageLoadingListener != null) {
            mOriginalImageLoadingListener.onLoadingCancelled(imageUri, view);
        }
    }
}
