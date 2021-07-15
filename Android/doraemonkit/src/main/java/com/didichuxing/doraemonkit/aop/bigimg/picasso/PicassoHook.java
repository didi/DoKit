package com.didichuxing.doraemonkit.aop.bigimg.picasso;

import com.didichuxing.doraemonkit.util.ReflectUtils;
import com.squareup.picasso.Request;
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
    public static void proxy(Object request) {
        try {
            if (request instanceof Request) {
                Request requestObj = (Request) request;
                List<Transformation> transformations = requestObj.transformations;
                if (transformations == null) {
                    transformations = new ArrayList<>();
                    transformations.add(new DokitPicassoTransformation(requestObj.uri, requestObj.resourceId));
                } else {
                    transformations.clear();
                    transformations.add(new DokitPicassoTransformation(requestObj.uri, requestObj.resourceId));
                }
                ReflectUtils.reflect(request).field("transformations", transformations);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}
