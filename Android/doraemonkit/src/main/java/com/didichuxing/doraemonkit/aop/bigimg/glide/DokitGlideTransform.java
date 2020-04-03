package com.didichuxing.doraemonkit.aop.bigimg.glide;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;

import com.blankj.utilcode.util.ReflectUtils;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.didichuxing.doraemonkit.kit.largepicture.LargePictureManager;
import com.didichuxing.doraemonkit.util.LogHelper;

import java.security.MessageDigest;

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020/4/3-13:04
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class DokitGlideTransform extends BitmapTransformation {
    private static final String ID = "com.didichuxing.doraemonkit.aop.bigimg.glide.DokitGlideTransform";
    private static final byte[] ID_BYTES = ID.getBytes(CHARSET);
    private static final String TAG = "DokitGlideTransform";
    private Object mRequestBuilder;

    public DokitGlideTransform(Object mRequestBuilder) {
        this.mRequestBuilder = mRequestBuilder;
    }

    @Override
    protected Bitmap transform(@NonNull BitmapPool pool, @NonNull Bitmap toTransform, int outWidth, int outHeight) {
        LogHelper.i(TAG, "===transform=====");
        String url = "";
        if (mRequestBuilder instanceof RequestBuilder) {
            url = ReflectUtils.reflect(mRequestBuilder).field("model").get();
        }
        toTransform = LargePictureManager.getInstance().transform(url, toTransform, false, "Glide");
        return toTransform;
    }

    @Override
    public void updateDiskCacheKey(@NonNull MessageDigest messageDigest) {
        messageDigest.update(ID_BYTES);
    }
}
