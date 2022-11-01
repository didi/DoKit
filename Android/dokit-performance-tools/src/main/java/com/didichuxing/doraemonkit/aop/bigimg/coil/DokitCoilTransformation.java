package com.didichuxing.doraemonkit.aop.bigimg.coil;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.didichuxing.doraemonkit.config.PerformanceSpInfoConfig;
import com.didichuxing.doraemonkit.constant.MemoryConstants;
import com.didichuxing.doraemonkit.kit.largepicture.LargePictureManager;
import com.didichuxing.doraemonkit.util.ConvertUtils;

import coil.bitmap.BitmapPool;
import coil.size.Size;
import coil.transform.Transformation;
import kotlin.coroutines.Continuation;

/**
 * ================================================
 * 作    者：mikaelzero
 * 版    本：1.0
 * 创建日期：2021/9/17
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class DokitCoilTransformation implements Transformation {
    private static final String TAG = "DokitTransformation";
    private Object mUri;

    public DokitCoilTransformation(Object uri) {
        this.mUri = uri;
    }


    @Override
    public String key() {
        return "Dokit&Coil&LargeBitmapTransformation";
    }

    @Nullable
    @Override
    public Object transform(@NonNull BitmapPool bitmapPool, @NonNull Bitmap source, @NonNull Size size, @NonNull Continuation<? super Bitmap> continuation) {
        try {
            if (PerformanceSpInfoConfig.isLargeImgOpen()) {
                double imgSize = ConvertUtils.byte2MemorySize(source.getByteCount(), MemoryConstants.MB);
                if (mUri != null) {
                    LargePictureManager.getInstance().saveImageInfo(mUri.toString(), imgSize, source.getWidth(), source.getHeight(), "Coil");
                } else {
                    LargePictureManager.getInstance().saveImageInfo("Null Uri", imgSize, source.getWidth(), source.getHeight(), "Coil");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return source;
    }
}
