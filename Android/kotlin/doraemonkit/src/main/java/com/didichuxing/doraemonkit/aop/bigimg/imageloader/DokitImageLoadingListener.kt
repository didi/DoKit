package com.didichuxing.doraemonkit.aop.bigimg.imageloader

import android.graphics.Bitmap
import android.view.View
import com.blankj.utilcode.constant.MemoryConstants
import com.blankj.utilcode.util.ConvertUtils
import com.didichuxing.doraemonkit.config.PerformanceSpInfoConfig
import com.didichuxing.doraemonkit.kit.largepicture.LargePictureManager
import com.nostra13.universalimageloader.core.assist.FailReason
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020/3/23-15:25
 * 描    述：
 * 修订历史：
 * ================================================
 */
class DokitImageLoadingListener(
        /**
         * 原始的ImageLoadingListener
         */
        private val mOriginalImageLoadingListener: ImageLoadingListener?) : ImageLoadingListener {
    override fun onLoadingStarted(imageUri: String, view: View) {
        mOriginalImageLoadingListener?.onLoadingStarted(imageUri, view)
    }

    override fun onLoadingFailed(imageUri: String, view: View, failReason: FailReason) {
        mOriginalImageLoadingListener?.onLoadingFailed(imageUri, view, failReason)
    }

    override fun onLoadingComplete(imageUri: String, view: View, loadedImage: Bitmap) {

        try {
            if (PerformanceSpInfoConfig.isLargeImgOpen()) {
                val imgSize = ConvertUtils.byte2MemorySize(loadedImage.byteCount.toLong(), MemoryConstants.MB)
                LargePictureManager.getInstance().saveImageInfo(imageUri, imgSize, loadedImage.width, loadedImage.height, "ImageLoader")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        mOriginalImageLoadingListener?.onLoadingComplete(imageUri, view, loadedImage)
    }

    override fun onLoadingCancelled(imageUri: String, view: View) {
        mOriginalImageLoadingListener?.onLoadingCancelled(imageUri, view)
    }

    companion object {
        private const val TAG = "DokitImageLoadingListener"
    }

}