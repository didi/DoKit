package com.didichuxing.doraemonkit.aop.bigimg.glide;

import android.graphics.Bitmap;

import com.bumptech.glide.load.Transformation;

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020/4/3-13:03
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class GlideTransformHook {
    public static Transformation<Bitmap> transform(Object baseRequestOptions, Object transformation) {
        return new DokitGlideTransform(baseRequestOptions, transformation);
    }
}
