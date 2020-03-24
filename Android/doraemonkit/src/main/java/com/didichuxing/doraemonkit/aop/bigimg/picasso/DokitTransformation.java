package com.didichuxing.doraemonkit.aop.bigimg.picasso;

import android.graphics.Bitmap;
import android.net.Uri;

import com.didichuxing.doraemonkit.kit.largepicture.LargePictureManager;
import com.squareup.picasso.Transformation;

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020/3/23-13:54
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class DokitTransformation implements Transformation {
    private static final String TAG = "DokitTransformation";
    private Uri mUri;

    public DokitTransformation(Uri uri) {
        this.mUri = uri;
    }

    @Override
    public Bitmap transform(Bitmap source) {
        source = LargePictureManager.getInstance().transform(mUri.toString(), source, true, "Picasso");
        return source;
    }

    @Override
    public String key() {
        return "Dokit&Picasso&LargeBitmapTransformation";
    }
}
