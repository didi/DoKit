package com.didichuxing.doraemonkit.aop.bigimg.imageloader

import com.nostra13.universalimageloader.core.listener.ImageLoadingListener

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020/3/23-17:20
 * 描    述：注入到com.nostra13.universalimageloader.core&ImageLoadingInfo的构造方法中
 * 修订历史：
 * ================================================
 */
public object ImageLoaderHook {
    @JvmStatic
    public fun proxy(imageLoadingListener: ImageLoadingListener?): ImageLoadingListener {
        return DokitImageLoadingListener(imageLoadingListener)
    }
}