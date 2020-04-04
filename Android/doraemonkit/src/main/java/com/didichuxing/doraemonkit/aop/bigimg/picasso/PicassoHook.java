package com.didichuxing.doraemonkit.aop.bigimg.picasso;

import android.net.Uri;


import com.squareup.picasso.Transformation;

import java.util.ArrayList;
import java.util.List;

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020/3/23-13:45
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class PicassoHook {

    /**
     * 注入到com.squareup.picasso.Request 构造方法中
     */
    public static List<Transformation> proxy(Uri uri, List<Transformation> transformations) {
        try {
            if (transformations == null) {
                transformations = new ArrayList<>();
                transformations.add(new DokitPicassoTransformation(uri));
            } else {
                transformations.add(new DokitPicassoTransformation(uri));
            }
            return transformations;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;

    }
}
