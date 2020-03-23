package com.didichuxing.doraemondemo.util;

import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.support.annotation.Nullable;

import com.didichuxing.doraemonkit.kit.largepicture.fresco.LargeBitmapFrescoProcessor;
import com.didichuxing.doraemonkit.ui.realtime.datasource.IDataSource;
import com.didichuxing.doraemonkit.util.LogHelper;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeControllerBuilder;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

import jp.wasabeef.fresco.processors.CombinePostProcessors;

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2019-09-26-15:55
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class FrescoUtil {
    private static final String TAG = "FrescoUtil";
    /**
     * 加载图片核心方法
     *
     * @param simpleDraweeView 图片加载控件
     * @param url              图片加载地址
     */
    public static void loadImage(SimpleDraweeView simpleDraweeView, String url) {
        //设置Hierarchy
        setHierarchay(simpleDraweeView.getHierarchy());
        //构建并获取ImageRequest
        ImageRequest imageRequest = getImageRequest(url, simpleDraweeView);
        //构建并获取Controller
        DraweeController draweeController = getController(imageRequest, simpleDraweeView.getController());
        //开始加载
        simpleDraweeView.setController(draweeController);
    }

    /**
     * 构建、获取Controller
     *
     * @param request
     * @param oldController
     * @return
     */
    private static DraweeController getController(ImageRequest request, @Nullable DraweeController oldController) {
        PipelineDraweeControllerBuilder builder = Fresco.newDraweeControllerBuilder();
        builder.setImageRequest(request);//设置图片请求
        builder.setTapToRetryEnabled(false);//设置是否允许加载失败时点击再次加载
        builder.setAutoPlayAnimations(false);//设置是否允许动画图自动播放
        builder.setOldController(oldController);
        builder.setControllerListener(new BaseControllerListener<>());
        return builder.build();
    }

    /**
     * 构建、获取ImageRequest
     *
     * @param url              加载路径
     * @param simpleDraweeView 加载的图片控件
     * @return ImageRequest
     */

    private static ImageRequest getImageRequest(String url, SimpleDraweeView simpleDraweeView) {

        //根据请求路径生成ImageRequest的构造者
        ImageRequestBuilder builder = ImageRequestBuilder.newBuilderWithSource(Uri.parse(url));

        //设置是否开启渐进式加载，仅支持JPEG图片
        builder.setProgressiveRenderingEnabled(true);
        //图片变换处理
        CombinePostProcessors.Builder processorBuilder = new CombinePostProcessors.Builder();
        //加入大图检测变换
        processorBuilder.add(new LargeBitmapFrescoProcessor(url));
        //应用加入的变换
        builder.setPostprocessor(processorBuilder.build());
        //更多图片变换请查看https://github.com/wasabeef/fresco-processors
        return builder.build();
    }

    //对Hierarchy进行设置，如各种状态下显示的图片
    private static void setHierarchay(GenericDraweeHierarchy hierarchy) {
        if (hierarchy != null) {
            //重新加载显示的图片
            //hierarchy.setRetryImage(retryImage);
            //加载失败显示的图片
            //hierarchy.setFailureImage(failureImage, ScalingUtils.ScaleType.CENTER_CROP);
            //加载完成前显示的占位图
            //hierarchy.setPlaceholderImage(placeholderImage, ScalingUtils.ScaleType.CENTER_CROP);
            //设置加载成功后图片的缩放模式
            hierarchy.setActualImageScaleType(ScalingUtils.ScaleType.FIT_CENTER);
            //显示加载进度条，使用自带的new ProgressBarDrawable()
            //默认会显示在图片的底部，可以设置进度条的颜色。
            //hierarchy.setProgressBarImage(new ProgressBarDrawable());
            //设置图片加载为圆形
            //hierarchy.setRoundingParams(RoundingParams.asCircle());
            //设置图片加载为圆角，并可设置圆角大小
            //hierarchy.setRoundingParams(RoundingParams.fromCornersRadius(radius));
            //其他设置请查看具体API。
        }
    }


}
