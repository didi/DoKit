package com.didichuxing.doraemonkit.aop.bigimg.imageloader;

import android.graphics.Bitmap;

import androidx.annotation.Nullable;

import android.view.View;

import com.didichuxing.doraemonkit.constant.MemoryConstants;
import com.didichuxing.doraemonkit.util.ConvertUtils;
import com.didichuxing.doraemonkit.config.PerformanceSpInfoConfig;
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
        try {
            if (PerformanceSpInfoConfig.isLargeImgOpen()) {
                double imgSize = ConvertUtils.byte2MemorySize(loadedImage.getByteCount(), MemoryConstants.MB);
                LargePictureManager.getInstance().saveImageInfo(imageUri, imgSize, loadedImage.getWidth(), loadedImage.getHeight(), "ImageLoader");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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
