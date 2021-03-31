package com.didichuxing.doraemonkit.kit.largepicture;

import com.didichuxing.doraemonkit.util.ActivityUtils;
import com.didichuxing.doraemonkit.config.PerformanceSpInfoConfig;
import com.didichuxing.doraemonkit.kit.core.UniversalActivity;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: linjizong
 * 2019-07-05
 * @desc: 大图检测管理类
 */
public class LargePictureManager {
    public static float MEMORY_DEFAULT_THRESHOLD = 1.0f;
    public static float FILE_DEFAULT_THRESHOLD = 150.0f;
    private double fileThreshold = PerformanceSpInfoConfig.getLargeImgFileThreshold(FILE_DEFAULT_THRESHOLD);
    private double memoryThreshold = PerformanceSpInfoConfig.getLargeImgMemoryThreshold(MEMORY_DEFAULT_THRESHOLD);
    private static final String TAG = "LargePictureManager";
    private DecimalFormat mDecimalFormat = new DecimalFormat("0.00");

    public void setFileThreshold(double fileThreshold) {
        this.fileThreshold = fileThreshold;
    }

    public void setMemoryThreshold(double memoryThreshold) {
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


    /**
     * @param url
     * @param size
     */
    public void process(String url, int size) {
        if (ActivityUtils.getTopActivity() instanceof UniversalActivity) {
            return;
        }
        if (PerformanceSpInfoConfig.isLargeImgOpen()) {
            //转化成kb
            double fileSize = (double) (size / 1024.0);
            saveImageInfo(url, fileSize);
        }

    }

    /**
     * 保存网络图片信息
     *
     * @param url
     * @param fileSize
     */
    private void saveImageInfo(String url, double fileSize) {
        if (ActivityUtils.getTopActivity() instanceof UniversalActivity) {
            return;
        }
        LargeImageInfo largeImageInfo;
        if (LARGE_IMAGE_INFO_MAP.containsKey(url)) {
            largeImageInfo = LARGE_IMAGE_INFO_MAP.get(url);
        } else {
            largeImageInfo = new LargeImageInfo();
            LARGE_IMAGE_INFO_MAP.put(url, largeImageInfo);
            largeImageInfo.setUrl(url);
        }
        largeImageInfo.setFileSize(fileSize);
    }


    /**
     * 保存在内部里的图片信息
     *
     * @param url
     * @param memorySize
     * @param width
     * @param height
     */
    public void saveImageInfo(String url, double memorySize, int width, int height, String framework) {
        if (ActivityUtils.getTopActivity() instanceof UniversalActivity) {
            return;
        }
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
        largeImageInfo.setFramework(framework);
    }

//    public Bitmap transform(String imageUrl, BitmapDrawable bitmapDrawable, boolean isPicasso, String framework) {
//        Bitmap sourceBitmap = ImageUtils.drawable2Bitmap(bitmapDrawable);
//        return transform(imageUrl, sourceBitmap, isPicasso, framework);
//    }


//    public Bitmap transform(String imageUrl, Bitmap sourceBitmap, boolean isPicasso, String framework) {
//        if (ActivityUtils.getTopActivity() instanceof UniversalActivity) {
//            return sourceBitmap;
//        }
//        if (PerformanceSpInfoConfig.isLargeImgOpen()) {
//            double imgSize = ConvertUtils.byte2MemorySize(sourceBitmap.getByteCount(), MemoryConstants.MB);
//            String strImageSize = mDecimalFormat.format(imgSize) + "MB";
//            //picasso需要创建新的bitmap进行操作
//            if (isPicasso) {
//                Bitmap newBitmap = Bitmap.createBitmap(sourceBitmap);
//                saveImageInfo(imageUrl, imgSize, newBitmap.getWidth(), newBitmap.getHeight(), framework);
//                if (imgSize > memoryThreshold) {
//                    newBitmap = ImageUtils.addTextWatermark(newBitmap, "MS:" + strImageSize, ConvertUtils.sp2px(16), Color.RED, newBitmap.getWidth() / 2 - ConvertUtils.sp2px(16) * strImageSize.length() / 2, newBitmap.getHeight() / 2);
//                    sourceBitmap.recycle();
//                    return newBitmap;
//                } else {
//                    return sourceBitmap;
//                }
//
//            }
//
//            saveImageInfo(imageUrl, imgSize, sourceBitmap.getWidth(), sourceBitmap.getHeight(), framework);
//            if (imgSize > memoryThreshold) {
//                sourceBitmap = ImageUtils.addTextWatermark(sourceBitmap, "MS:" + strImageSize, ConvertUtils.sp2px(16), Color.RED, sourceBitmap.getWidth() / 2 - ConvertUtils.sp2px(16) * strImageSize.length() / 2, sourceBitmap.getHeight() / 2);
//            }
//            return sourceBitmap;
//        }
//        return sourceBitmap;
//    }

}
