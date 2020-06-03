package com.didichuxing.doraemonkit.aop.bigimg.glide

import android.content.Context
import android.graphics.Bitmap
import com.bumptech.glide.load.Key
import com.bumptech.glide.load.Transformation
import com.bumptech.glide.load.engine.Resource
import java.security.MessageDigest

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020/4/3-13:04
 * 描    述：
 * 修订历史：
 * ================================================
 */
class DokitGlideTransform(private val mRequestBuilder: Any, transformation: Any?) : Transformation<Bitmap?> {
    private var mWrap: Transformation<Bitmap>? = null
    override fun updateDiskCacheKey(messageDigest: MessageDigest) {
        if (mWrap != null) {
            mWrap?.updateDiskCacheKey(messageDigest)
        } else {
            messageDigest.update(ID_BYTES)
        }
    }

    override fun equals(o: Any?): Boolean {
        return if (mWrap != null) {
            mWrap == o
        } else false
    }

    override fun hashCode(): Int {
        return mWrap?.hashCode() ?: 0
    }

    override fun transform(context: Context, resource: Resource<Bitmap?>, outWidth: Int, outHeight: Int): Resource<Bitmap?> {
        //TODO("功能待实现")
//        var resource = resource
//        try {
//            if (mWrap != null) {
//                resource = mWrap.transform(context, resource, outWidth, outHeight)
//            }
//            if (PerformanceSpInfoConfig.isLargeImgOpen()) {
//                var url: String? = ""
//                if (mRequestBuilder is RequestBuilder<*>) {
//                    if (ReflectUtils.reflect(mRequestBuilder).field("model").get<Any>() is String) {
//                        url = ReflectUtils.reflect(mRequestBuilder).field("model").get()
//                    } else if (ReflectUtils.reflect(mRequestBuilder).field("model").get<Any>() is Int) {
//                        url = "" + ReflectUtils.reflect(mRequestBuilder).field("model").get()
//                    }
//                }
//                val bitmap = resource.get()
//                val imgSize = ConvertUtils.byte2MemorySize(bitmap.byteCount.toLong(), MemoryConstants.MB)
//                LargePictureManager.getInstance().saveImageInfo(url, imgSize, bitmap.width, bitmap.height, "Glide")
//            }
//        } catch (e: Exception) {
//            if (mWrap != null) {
//                resource = mWrap.transform(context, resource, outWidth, outHeight)
//            }
//        }
        return resource
    }

    companion object {
        private const val ID = "com.didichuxing.doraemonkit.aop.bigimg.glide.DokitGlideTransform"
        private val ID_BYTES = ID.toByteArray(Key.CHARSET)
        private const val TAG = "DokitGlideTransform"
    }

    init {
        if (transformation is Transformation<*>) {
            mWrap = transformation as Transformation<Bitmap>
        }
    }
}