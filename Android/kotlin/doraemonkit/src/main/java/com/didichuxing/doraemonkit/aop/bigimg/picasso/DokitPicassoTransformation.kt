package com.didichuxing.doraemonkit.aop.bigimg.picasso

import android.graphics.Bitmap
import android.net.Uri
import com.squareup.picasso.Transformation

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020/3/23-13:54
 * 描    述：
 * 修订历史：
 * ================================================
 */
class DokitPicassoTransformation(private val mUri: Uri?, private val mResourceId: Int) : Transformation {
    override fun transform(source: Bitmap): Bitmap {
        //TODO("功能待实现")
//        try {
//            if (PerformanceSpInfoConfig.isLargeImgOpen()) {
//                if (mUri != null) {
//                    val imgSize = ConvertUtils.byte2MemorySize(source.byteCount.toLong(), MemoryConstants.MB)
//                    LargePictureManager.getInstance().saveImageInfo(mUri.toString(), imgSize, source.width, source.height, "Picasso")
//                } else {
//                    val imgSize = ConvertUtils.byte2MemorySize(source.byteCount.toLong(), MemoryConstants.MB)
//                    LargePictureManager.getInstance().saveImageInfo("" + mResourceId, imgSize, source.width, source.height, "Picasso")
//                }
//            }
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
        return source
    }

    override fun key(): String {
        return "Dokit&Picasso&LargeBitmapTransformation"
    }

    companion object {
        private const val TAG = "DokitTransformation"
    }

}