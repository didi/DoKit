package com.didichuxing.doraemonkit.kit.largepicture;

import android.graphics.Bitmap;
import android.graphics.Color;

import com.blankj.utilcode.constant.MemoryConstants;
import com.blankj.utilcode.util.ConvertUtils;
import com.blankj.utilcode.util.ImageUtils;
import com.didichuxing.doraemonkit.DoraemonKit;
import com.didichuxing.doraemonkit.config.PerformanceSpInfoConfig;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: linjizong
 * @date: 2019-07-05
 * @desc: 大图检测管理类
 */
public class LargePictureManager {
    public static float MEMORY_DEFAULT_THRESHOLD = 1.0f;
    public static float FILE_DEFAULT_THRESHOLD = 150.0f;
    private float fileThreshold = PerformanceSpInfoConfig.getLargeImgFileThreshold(DoraemonKit.APPLICATION, FILE_DEFAULT_THRESHOLD);
    private float memoryThreshold = PerformanceSpInfoConfig.getLargeImgMemoryThreshold(DoraemonKit.APPLICATION, MEMORY_DEFAULT_THRESHOLD);
    private static final String TAG = "LargePictureManager";
    private DecimalFormat mDecimalFormat = new DecimalFormat("#.00");

    public void setFileThreshold(float fileThreshold) {
        this.fileThreshold = fileThreshold;
    }

    public void setMemoryThreshold(float memoryThreshold) {
        this.memoryThreshold = memoryThreshold;
    }

    /**
     * 保存大图信息
     */
    public static Map<String, LargeImageInfo> LARGE_IMAGE_INFO_MAP = new HashMap<>();

    public static LargePictureManager getInstance() {
        return Holder.INSTANCE;
    }

    private static class Holder {
        private static LargePictureManager INSTANCE = new LargePictureManager();
    }

    private LargePictureManager() {
    }


    Map<String, String> imgMap;

    /**
     * @param url
     * @param size
     */
    public void process(String url, int size) {
        if (PerformanceSpInfoConfig.isLargeImgOpen()) {
            float fileSize = (float) (size / 1024.0);
            if (fileSize > fileThreshold) {
                saveImageInfo(url, mDecimalFormat.format(fileSize) + "KB");
                //Log.e(TAG, url + ": " + fileSize + "kb");
            } else {
                saveImageInfo(url, "<" + fileThreshold + "KB");
            }
        }

    }

    /**
     * 保存图片信息
     *
     * @param url
     * @param fileSize
     */
    private void saveImageInfo(String url, String fileSize) {
        LargeImageInfo largeImageInfo;
        if (LARGE_IMAGE_INFO_MAP.containsKey(url)) {
            largeImageInfo = LARGE_IMAGE_INFO_MAP.get(url);
        } else {
            largeImageInfo = new LargeImageInfo();
            LARGE_IMAGE_INFO_MAP.put(url, largeImageInfo);
            largeImageInfo.setUrl(url);
        }
        largeImageInfo.setFileSize(fileSize);
        largeImageInfo.setFrom("network");
    }


    private void saveImageInfo(String url, String memorySize, int width, int height) {
        LargeImageInfo largeImageInfo;
        if (LARGE_IMAGE_INFO_MAP.containsKey(url)) {
            largeImageInfo = LARGE_IMAGE_INFO_MAP.get(url);
        } else {
            largeImageInfo = new LargeImageInfo();
            LARGE_IMAGE_INFO_MAP.put(url, largeImageInfo);
            largeImageInfo.setUrl(url);

        }
        largeImageInfo.setMemorySize(memorySize);
        largeImageInfo.setWidth(width);
        largeImageInfo.setHeight(height);
        largeImageInfo.setFrom("memory");
    }


    public Bitmap transform(String imageUrl, Bitmap sourceBitmap, boolean isPicasso) {
        if (PerformanceSpInfoConfig.isLargeImgOpen()) {
            double imgSize = ConvertUtils.byte2MemorySize(sourceBitmap.getByteCount(), MemoryConstants.MB);
            //picasso需要创建新的bitmap进行操作
            if (isPicasso) {
                if (imgSize > memoryThreshold) {
                    Bitmap newBitmap = Bitmap.createBitmap(sourceBitmap);
                    String strImageSize = mDecimalFormat.format(imgSize) + "MB";
                    saveImageInfo(imageUrl, strImageSize, newBitmap.getWidth(), newBitmap.getHeight());
                    newBitmap = ImageUtils.addTextWatermark(newBitmap, "MemorySize:" + strImageSize, ConvertUtils.sp2px(16), Color.RED, newBitmap.getWidth() / 2 - ConvertUtils.sp2px(16) * strImageSize.length() / 2, newBitmap.getHeight() / 2);
                    sourceBitmap.recycle();
                    return newBitmap;
                } else {
                    return sourceBitmap;
                }


            }

            if (imgSize > memoryThreshold) {
                String strImageSize = mDecimalFormat.format(imgSize) + "MB";
                saveImageInfo(imageUrl, strImageSize, sourceBitmap.getWidth(), sourceBitmap.getHeight());
                sourceBitmap = ImageUtils.addTextWatermark(sourceBitmap, "MemorySize:" + strImageSize, ConvertUtils.sp2px(16), Color.RED, sourceBitmap.getWidth() / 2 - ConvertUtils.sp2px(16) * strImageSize.length() / 2, sourceBitmap.getHeight() / 2);
            }
            return sourceBitmap;
        }
        return sourceBitmap;
    }

}
