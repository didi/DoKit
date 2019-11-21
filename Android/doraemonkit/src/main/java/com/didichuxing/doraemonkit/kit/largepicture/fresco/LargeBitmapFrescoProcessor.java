package com.didichuxing.doraemonkit.kit.largepicture.fresco;

import android.graphics.Bitmap;

import com.didichuxing.doraemonkit.kit.largepicture.LargePictureManager;
import com.facebook.imagepipeline.request.BasePostprocessor;

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
public class LargeBitmapFrescoProcessor extends BasePostprocessor {
    private String mImageUrl;

    public LargeBitmapFrescoProcessor(String mImageUrl) {
        this.mImageUrl = mImageUrl;
    }

    /**
     * @param destBitmap   输出的bitmap
     * @param sourceBitmap 输入的bitmap
     */
    @Override
    public void process(Bitmap destBitmap, Bitmap sourceBitmap) {
        sourceBitmap = LargePictureManager.getInstance().transform(mImageUrl, sourceBitmap, false);
        super.process(destBitmap, sourceBitmap);
    }
}
