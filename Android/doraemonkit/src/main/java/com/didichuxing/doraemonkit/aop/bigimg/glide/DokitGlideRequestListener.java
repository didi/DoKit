package com.didichuxing.doraemonkit.aop.bigimg.glide;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import androidx.annotation.Nullable;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.didichuxing.doraemonkit.kit.largepicture.LargePictureManager;

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020/3/20-18:23
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class DokitGlideRequestListener<R> implements RequestListener<R> {
    private static final String TAG = "DokitGlideListener";

    @Override
    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<R> target, boolean isFirstResource) {
        return false;
    }

    @Override
    public boolean onResourceReady(R resource, Object model, Target<R> target, DataSource dataSource, boolean isFirstResource) {
        if (resource instanceof Bitmap) {
            LargePictureManager.getInstance().transform(model.toString(), (Bitmap) resource, false, "Glide");
        } else if (resource instanceof BitmapDrawable) {
            LargePictureManager.getInstance().transform(model.toString(), (BitmapDrawable) resource, false, "Glide");
        }
        return false;
    }


}
