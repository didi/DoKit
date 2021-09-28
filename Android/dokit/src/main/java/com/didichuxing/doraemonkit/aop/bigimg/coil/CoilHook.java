package com.didichuxing.doraemonkit.aop.bigimg.coil;

import android.util.Log;

import com.didichuxing.doraemonkit.util.ReflectUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import coil.request.ImageRequest;
import coil.transform.Transformation;
import okhttp3.Interceptor;

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
     */

    public static void proxy(Object request) {
        Log.e("CoilHook", "CoilHook proxy");
        try {
            if (request instanceof ImageRequest) {
                ImageRequest requestObj = (ImageRequest) request;
                List<Transformation> transformations = new ArrayList<>(requestObj.getTransformations());
                if (!hasDoKitTransformation(transformations)) {
                    transformations.add(new DokitCoilTransformation(requestObj.getData()));
                    ReflectUtils.reflect(request).field("transformations", transformations);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private static boolean hasDoKitTransformation(List<Transformation> transformations) {
        for (Transformation transformation : transformations) {
            if (transformation instanceof DokitCoilTransformation) {
                return true;
            }
        }
        return false;
    }

}
