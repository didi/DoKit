package com.didichuxing.doraemonkit.aop.bigimg.glide;

import android.content.Context;
import android.graphics.Bitmap;

import androidx.annotation.NonNull;

import com.didichuxing.doraemonkit.constant.MemoryConstants;
import com.didichuxing.doraemonkit.util.ConvertUtils;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.engine.Resource;
import com.didichuxing.doraemonkit.config.PerformanceSpInfoConfig;
import com.didichuxing.doraemonkit.kit.largepicture.LargePictureManager;
import com.didichuxing.doraemonkit.util.ReflectUtils;

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
public class DokitGlideTransform implements Transformation<Bitmap> {
    private static final String ID = "com.didichuxing.doraemonkit.aop.bigimg.glide.DokitGlideTransform";
    private static final byte[] ID_BYTES = ID.getBytes(CHARSET);
    private static final String TAG = "DokitGlideTransform";
    private Object mRequestBuilder;
    private Transformation mWrap;

    public DokitGlideTransform(Object mRequestBuilder, Object transformation) {
        this.mRequestBuilder = mRequestBuilder;
        if (transformation instanceof Transformation) {
            this.mWrap = (Transformation) transformation;
        }
    }


    @Override
    public void updateDiskCacheKey(@NonNull MessageDigest messageDigest) {
        if (mWrap != null) {
            mWrap.updateDiskCacheKey(messageDigest);
        } else {
            messageDigest.update(ID_BYTES);
        }
    }


    @Override
    public boolean equals(Object o) {
        if (mWrap != null) {
            return mWrap.equals(o);
        }
        return false;
    }

    @Override
    public int hashCode() {
        if (mWrap != null) {
            return mWrap.hashCode();
        }
        return 0;
    }

    @NonNull
    @Override
    public Resource<Bitmap> transform(@NonNull Context context, @NonNull Resource<Bitmap> resource, int outWidth, int outHeight) {
        try {
            if (mWrap != null) {
                resource = mWrap.transform(context, resource, outWidth, outHeight);
            }

            if (PerformanceSpInfoConfig.isLargeImgOpen()) {
                String url = "";
                if (mRequestBuilder instanceof RequestBuilder) {
                    if (ReflectUtils.reflect(mRequestBuilder).field("model").get() instanceof String) {
                        url = ReflectUtils.reflect(mRequestBuilder).field("model").get();
                    } else if (ReflectUtils.reflect(mRequestBuilder).field("model").get() instanceof Integer) {
                        url = "" + ReflectUtils.reflect(mRequestBuilder).field("model").get();
                    }
                }
                Bitmap bitmap = resource.get();
                double imgSize = ConvertUtils.byte2MemorySize(bitmap.getByteCount(), MemoryConstants.MB);
                LargePictureManager.getInstance().saveImageInfo(url, imgSize, bitmap.getWidth(), bitmap.getHeight(), "Glide");
            }
        } catch (Exception e) {
            if (mWrap != null) {
                resource = mWrap.transform(context, resource, outWidth, outHeight);
            }
        }

        return resource;
    }
}
