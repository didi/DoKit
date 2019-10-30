package com.didichuxing.doraemonkit.kit.largepicture.glide;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

import java.security.MessageDigest;

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2019-09-25-17:43
 * 描    述：Glide大图检测的bitmap
 * Glide根据你最终的控件大小会预先进行一波bitmap的裁剪 picasso则不会 所以glide比picasso在内存管理上更加高效
 * 修订历史：
 * ================================================
 */
public class LargeBitmapGlideTransformation extends BitmapTransformation {
    private static final String TAG = "LargeBitmapTransformation";
    private static final String ID = "com.bumptech.glide.transformations.LargeBitmapTransformation";
    private static final byte[] ID_BYTES = ID.getBytes(CHARSET);
    private String mImageUrl;

    public LargeBitmapGlideTransformation(String imageUrl) {
        this.mImageUrl = imageUrl;
    }

    @Override
    protected Bitmap transform(@NonNull BitmapPool pool, @NonNull Bitmap toTransform, int outWidth, int outHeight) {
        return toTransform;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof LargeBitmapGlideTransformation;
    }

    @Override
    public int hashCode() {
        return ID.hashCode();
    }

    @Override
    public void updateDiskCacheKey(@NonNull MessageDigest messageDigest) {
        messageDigest.update(ID_BYTES);
    }
}
