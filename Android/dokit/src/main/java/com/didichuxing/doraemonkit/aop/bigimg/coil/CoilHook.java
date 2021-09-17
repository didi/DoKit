package com.didichuxing.doraemonkit.aop.bigimg.coil;

import android.util.Log;

import com.didichuxing.doraemonkit.util.ReflectUtils;

import java.util.ArrayList;
import java.util.List;

import coil.request.ImageRequest;
import coil.transform.Transformation;

/**
 * ================================================
 * 作    者：mikaelzero
 * 版    本：1.0
 * 创建日期：2021/9/17
 * 描    述：注入到 coil.request.ImageRequest#init(方法中)
 * 修订历史：
 * ================================================
 */
public class CoilHook {
    /**
     * hook transformations
     *
     */

    public static void proxy(Object request) {
        Log.e("CoilHook", "CoilHook proxy");
        try {
            if (request instanceof ImageRequest) {
                ImageRequest requestObj = (ImageRequest) request;
                List<Transformation> transformations = requestObj.getTransformations();
                if (transformations.isEmpty()) {
                    transformations = new ArrayList<>();
                    transformations.add(new DokitCoilTransformation(requestObj.getData()));
                } else {
                    transformations.clear();
                    transformations.add(new DokitCoilTransformation(requestObj.getData()));
                }
                ReflectUtils.reflect(request).field("transformations", transformations);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
