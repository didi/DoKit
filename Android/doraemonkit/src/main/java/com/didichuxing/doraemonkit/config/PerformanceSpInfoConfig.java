package com.didichuxing.doraemonkit.config;

import android.content.Context;

import com.didichuxing.doraemonkit.DoraemonKit;
import com.didichuxing.doraemonkit.constant.SharedPrefsKey;
import com.didichuxing.doraemonkit.kit.largepicture.LargePictureManager;
import com.didichuxing.doraemonkit.util.SharedPrefsUtil;

/**
 * Created by wanglikun on 2018/9/14.
 * 将配置信息保存在sp中
 */

public class PerformanceSpInfoConfig {
    public static boolean isFPSOpen(Context context) {
//        return false;
        return SharedPrefsUtil.getBoolean(context, SharedPrefsKey.FRAME_INFO_FPS_OPEN, false);
    }

    public static void setFPSOpen(Context context, boolean open) {
        SharedPrefsUtil.putBoolean(context, SharedPrefsKey.FRAME_INFO_FPS_OPEN, open);
    }

    public static boolean isCPUOpen(Context context) {
//        return false;
        return SharedPrefsUtil.getBoolean(context, SharedPrefsKey.FRAME_INFO_CPU_OPEN, false);
    }

    public static void setCPUOpen(Context context, boolean open) {
        SharedPrefsUtil.putBoolean(context, SharedPrefsKey.FRAME_INFO_CPU_OPEN, open);
    }

    public static boolean isMemoryOpen(Context context) {
//        return false;
        return SharedPrefsUtil.getBoolean(context, SharedPrefsKey.FRAME_INFO_MEMORY_OPEN, false);
    }

    public static void setMemoryOpen(Context context, boolean open) {
        SharedPrefsUtil.putBoolean(context, SharedPrefsKey.FRAME_INFO_MEMORY_OPEN, open);
    }

    public static boolean isTrafficOpen(Context context) {
        return SharedPrefsUtil.getBoolean(context, SharedPrefsKey.FRAME_INFO_TRAFFIC_OPEN, false);
    }

    public static void setTrafficOpen(Context context, boolean open) {
        SharedPrefsUtil.putBoolean(context, SharedPrefsKey.FRAME_INFO_TRAFFIC_OPEN, open);
    }

    /**
     * 是否显示性能检测UI开关
     *
     * @param context
     * @return
     */
    public static boolean isFrameUiOpen(Context context) {
        return SharedPrefsUtil.getBoolean(context, SharedPrefsKey.FRAME_INFO_UI_OPEN, false);
    }

    /**
     * 设置显示性能检测UI开关
     *
     * @param context
     * @return
     */
    public static void setFrameUiOpen(Context context, boolean open) {
        SharedPrefsUtil.putBoolean(context, SharedPrefsKey.FRAME_INFO_UI_OPEN, open);
    }

    /**
     * 判断是否开启大图检测
     *
     * @return
     */
    public static boolean isLargeImgOpen() {
        return SharedPrefsUtil.getBoolean(DoraemonKit.APPLICATION, SharedPrefsKey.LARGE_IMG_OPEN, false);
    }

    /**
     * 设置打开或者关闭大图检测
     *
     * @param open
     */
    public static void setLargeImgOpen(boolean open) {
        SharedPrefsUtil.putBoolean(DoraemonKit.APPLICATION, SharedPrefsKey.LARGE_IMG_OPEN, open);
    }


    /**
     * 设置大图内存阈值
     *
     * @param context
     * @param threshold
     */
    public static void setLargeImgMemoryThreshold(Context context, float threshold) {
        SharedPrefsUtil.putFloat(context, SharedPrefsKey.LARGE_IMG_MEMORY_THRESHOLD, threshold);
        LargePictureManager.getInstance().setMemoryThreshold(threshold);
    }


    /**
     * 获得大图内存阈值
     *
     * @param context
     * @param threshold
     */
    public static float getLargeImgMemoryThreshold(Context context, float threshold) {
        return SharedPrefsUtil.getFloat(context, SharedPrefsKey.LARGE_IMG_MEMORY_THRESHOLD, threshold);
    }

    /**
     * 设置大图文件阈值
     *
     * @param context
     * @param threshold
     */
    public static void setLargeImgFileThreshold(Context context, float threshold) {
        SharedPrefsUtil.putFloat(context, SharedPrefsKey.LARGE_IMG_FILE_THRESHOLD, threshold);
        LargePictureManager.getInstance().setFileThreshold(threshold);
    }

    /**
     * 获得大图文件阈值
     *
     * @param context
     * @param threshold
     */
    public static float getLargeImgFileThreshold(Context context, float threshold) {
        return SharedPrefsUtil.getFloat(context, SharedPrefsKey.LARGE_IMG_FILE_THRESHOLD, threshold);
    }

}
