package com.didichuxing.doraemonkit.aop.img.glide;

import android.graphics.Bitmap;
import android.support.annotation.Nullable;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.didichuxing.doraemonkit.util.LogHelper;

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020/3/20-18:23
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class DokitGlideListener implements RequestListener<Bitmap> {
    private static final String TAG = "DokitGlideListener";

    @Override
    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
        return false;
    }

    @Override
    public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
        LogHelper.i(TAG, "====onResourceReady====");
        return false;
    }
}
