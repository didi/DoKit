package com.didichuxing.doraemonkit.aop.bigimg.fresco

import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import com.facebook.cache.common.CacheKey
import com.facebook.common.references.CloseableReference
import com.facebook.imagepipeline.bitmaps.PlatformBitmapFactory
import com.facebook.imagepipeline.nativecode.Bitmaps
import com.facebook.imagepipeline.request.BasePostprocessor
import com.facebook.imagepipeline.request.Postprocessor

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020/3/23-14:53
 * 描    述：
 * 修订历史：
 * ================================================
 */
class DokitFrescoPostprocessor(private val mUri: Uri, private val mOriginalPostprocessor: Postprocessor?) : Postprocessor {
    override fun process(sourceBitmap: Bitmap, bitmapFactory: PlatformBitmapFactory): CloseableReference<Bitmap> {
        try {
            //TODO("功能待实现")
//            if (PerformanceSpInfoConfig.isLargeImgOpen()) {
//                double imgSize = ConvertUtils.byte2MemorySize(sourceBitmap.getByteCount(), MemoryConstants.MB);
//                LargePictureManager.getInstance().saveImageInfo(mUri.toString(), imgSize, sourceBitmap.getWidth(), sourceBitmap.getHeight(), "Fresco");
//            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        if (mOriginalPostprocessor != null) {
            return mOriginalPostprocessor.process(sourceBitmap, bitmapFactory)
        }
        val sourceBitmapConfig = sourceBitmap.config
        val destBitmapRef = bitmapFactory.createBitmapInternal(
                sourceBitmap.width,
                sourceBitmap.height,
                sourceBitmapConfig ?: BasePostprocessor.FALLBACK_BITMAP_CONFIGURATION)
        return try {
            process(destBitmapRef.get(), sourceBitmap)
            CloseableReference.cloneOrNull(destBitmapRef)!!
        } finally {
            CloseableReference.closeSafely(destBitmapRef)
        }
    }

    fun process(destBitmap: Bitmap, sourceBitmap: Bitmap) {
        internalCopyBitmap(destBitmap, sourceBitmap)
        process(destBitmap)
    }

    fun process(bitmap: Bitmap?) {}
    override fun getName(): String {
        return if (mOriginalPostprocessor != null) {
            mOriginalPostprocessor.name
        } else "DoKit&Fresco&DokitPostprocessor"
    }

    override fun getPostprocessorCacheKey(): CacheKey? {
        return mOriginalPostprocessor?.postprocessorCacheKey
    }

    companion object {
        private const val TAG = "DokitPostprocessor"

        /**
         * Copies the content of `sourceBitmap` to `destBitmap`. Both bitmaps must have the
         * same width and height. If their [Bitmap.Config] are identical, the memory is directly
         * copied. Otherwise, the `sourceBitmap` is drawn into `destBitmap`.
         */
        private fun internalCopyBitmap(destBitmap: Bitmap, sourceBitmap: Bitmap) {
            if (destBitmap.config == sourceBitmap.config) {
                Bitmaps.copyBitmap(destBitmap, sourceBitmap)
            } else {
                // The bitmap configurations might be different when the source bitmap's configuration is
                // null, because it uses an internal configuration and the destination bitmap's configuration
                // is the FALLBACK_BITMAP_CONFIGURATION. This is the case for static images for animated GIFs.
                val canvas = Canvas(destBitmap)
                canvas.drawBitmap(sourceBitmap, 0f, 0f, null)
            }
        }
    }

}