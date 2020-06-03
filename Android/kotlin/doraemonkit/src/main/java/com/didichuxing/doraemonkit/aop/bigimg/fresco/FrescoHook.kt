package com.didichuxing.doraemonkit.aop.bigimg.fresco

import android.net.Uri
import com.facebook.imagepipeline.request.Postprocessor

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020/3/23-16:57
 * 描    述：在com.facebook.imagepipeline.request.ImageRequest&ImageRequest()中注入
 * 修订历史：
 * ================================================
 */
public object FrescoHook {
    @JvmStatic
    public fun proxy(uri: Uri?, postprocessor: Postprocessor?): Postprocessor {
        return DokitFrescoPostprocessor(uri!!, postprocessor)
    }
}