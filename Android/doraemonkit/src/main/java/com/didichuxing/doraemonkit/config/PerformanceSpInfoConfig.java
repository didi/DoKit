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
    public static boolean isFPSOpen() {
//        return false;
        return SharedPrefsUtil.getBoolean(SharedPrefsKey.FRAME_INFO_FPS_OPEN, false);
    }

    public static void setFPSOpen(boolean open) {
        SharedPrefsUtil.putBoolean(SharedPrefsKey.FRAME_INFO_FPS_OPEN, open);
    }

    public static boolean isCPUOpen() {
//        return false;
        return SharedPrefsUtil.getBoolean(SharedPrefsKey.FRAME_INFO_CPU_OPEN, false);
    }

    public static void setCPUOpen(boolean open) {
        SharedPrefsUtil.putBoolean(SharedPrefsKey.FRAME_INFO_CPU_OPEN, open);
    }

    public static boolean isMemoryOpen() {
//        return false;
        return SharedPrefsUtil.getBoolean(SharedPrefsKey.FRAME_INFO_MEMORY_OPEN, false);
    }

    public static void setMemoryOpen(boolean open) {
        SharedPrefsUtil.putBoolean(SharedPrefsKey.FRAME_INFO_MEMORY_OPEN, open);
    }

    public static boolean isTrafficOpen() {
        return SharedPrefsUtil.getBoolean(SharedPrefsKey.FRAME_INFO_TRAFFIC_OPEN, false);
    }

    public static void setTrafficOpen(boolean open) {
        SharedPrefsUtil.putBoolean(SharedPrefsKey.FRAME_INFO_TRAFFIC_OPEN, open);
    }

    /**
     * 是否显示性能检测UI开关
     *
     * @return
     */
    public static boolean isFrameUiOpen() {
        return SharedPrefsUtil.getBoolean(SharedPrefsKey.FRAME_INFO_UI_OPEN, false);
    }

    /**
     * 设置显示性能检测UI开关
     *
     * @return
     */
    public static void setFrameUiOpen(boolean open) {
        SharedPrefsUtil.putBoolean(SharedPrefsKey.FRAME_INFO_UI_OPEN, open);
    }

    /**
     * 判断是否开启大图检测
     *
     * @return
     */
    public static boolean isLargeImgOpen() {
        return SharedPrefsUtil.getBoolean(SharedPrefsKey.LARGE_IMG_OPEN, false);
    }

    /**
     * 设置打开或者关闭大图检测
     *
     * @param open
     */
    public static void setLargeImgOpen(boolean open) {
        SharedPrefsUtil.putBoolean(SharedPrefsKey.LARGE_IMG_OPEN, open);
    }


    /**
     * 设置大图内存阈值
     *
     * @param threshold
     */
    public static void setLargeImgMemoryThreshold(float threshold) {
        SharedPrefsUtil.putFloat(SharedPrefsKey.LARGE_IMG_MEMORY_THRESHOLD, threshold);
        LargePictureManager.getInstance().setMemoryThreshold(threshold);
    }


    /**
     * 获得大图内存阈值
     *
     * @param threshold
     */
    public static double getLargeImgMemoryThreshold(float threshold) {
        return SharedPrefsUtil.getFloat(SharedPrefsKey.LARGE_IMG_MEMORY_THRESHOLD, threshold);
    }

    /**
     * 设置大图文件阈值
     *
     * @param threshold
     */
    public static void setLargeImgFileThreshold(float threshold) {
        SharedPrefsUtil.putFloat(SharedPrefsKey.LARGE_IMG_FILE_THRESHOLD, threshold);
        LargePictureManager.getInstance().setFileThreshold(threshold);
    }

    /**
     * 获得大图文件阈值
     *
     * @param threshold
     */
    public static double getLargeImgFileThreshold(float threshold) {
        return SharedPrefsUtil.getFloat(SharedPrefsKey.LARGE_IMG_FILE_THRESHOLD, threshold);
    }

}
