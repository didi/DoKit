package com.didichuxing.doraemonkit.kit.largepicture.picasso;

import android.graphics.Bitmap;

import com.squareup.picasso.Transformation;

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2019-09-25-17:43
 * 描    述：Picasso大图检测的bitmap
 * 修订历史：
 * ================================================
 */
public class LargeBitmapPicassoTransformation implements Transformation {
    private String mImageUrl;

    public LargeBitmapPicassoTransformation(String imageUrl) {
        this.mImageUrl = imageUrl;
    }

    @Override
    public Bitmap transform(Bitmap source) {
        return source;
    }

    @Override
    public String key() {
        return "LargeBitmapTransformation";
    }
}
