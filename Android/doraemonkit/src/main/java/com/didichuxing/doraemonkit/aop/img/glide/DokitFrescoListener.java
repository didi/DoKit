package com.didichuxing.doraemonkit.aop.img.glide;

import android.graphics.drawable.Animatable;

import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.imagepipeline.image.ImageInfo;

import javax.annotation.Nullable;

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020/3/20-18:58
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class DokitFrescoListener extends BaseControllerListener<ImageInfo> {
    @Override
    public void onFinalImageSet(
            String id,
            @Nullable ImageInfo imageInfo,
            @Nullable Animatable animatable) {
    }
}
