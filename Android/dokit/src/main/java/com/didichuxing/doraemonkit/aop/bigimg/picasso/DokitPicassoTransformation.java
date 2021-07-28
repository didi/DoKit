package com.didichuxing.doraemonkit.aop.bigimg.picasso;

import android.graphics.Bitmap;
import android.net.Uri;

import com.didichuxing.doraemonkit.constant.MemoryConstants;
import com.didichuxing.doraemonkit.util.ConvertUtils;
import com.didichuxing.doraemonkit.config.PerformanceSpInfoConfig;
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
public class DokitPicassoTransformation implements Transformation {
    private static final String TAG = "DokitTransformation";
    private Uri mUri;
    private int mResourceId;

    public DokitPicassoTransformation(Uri uri, int resourceId) {
        this.mUri = uri;
        this.mResourceId = resourceId;
    }

    @Override
    public Bitmap transform(Bitmap source) {
        try {
            if (PerformanceSpInfoConfig.isLargeImgOpen()) {
                if (mUri != null) {
                    double imgSize = ConvertUtils.byte2MemorySize(source.getByteCount(), MemoryConstants.MB);
                    LargePictureManager.getInstance().saveImageInfo(mUri.toString(), imgSize, source.getWidth(), source.getHeight(), "Picasso");
                } else {
                    double imgSize = ConvertUtils.byte2MemorySize(source.getByteCount(), MemoryConstants.MB);
                    LargePictureManager.getInstance().saveImageInfo("" + mResourceId, imgSize, source.getWidth(), source.getHeight(), "Picasso");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return source;
    }

    @Override
    public String key() {
        return "Dokit&Picasso&LargeBitmapTransformation";
    }
}
