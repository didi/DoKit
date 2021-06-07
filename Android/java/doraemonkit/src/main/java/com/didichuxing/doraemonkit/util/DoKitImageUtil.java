package com.didichuxing.doraemonkit.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by wanglikun on 2018/10/30.
 */

public class DoKitImageUtil {
    private DoKitImageUtil() {
    }

    public static Bitmap decodeSampledBitmapFromFilePath(String imagePath,
                                                         int reqWidth, int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(imagePath,options);
    }

    public static int calculateInSampleSize(BitmapFactory.Options options,
                                            int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }

    /**
     * 获得像素点的信息
     * @param bitmap
     * @param x
     * @param y
     * @return
     */
    public static int getPixel(Bitmap bitmap, int x, int y) {
        if (bitmap == null) {
            return -1;
        }
        if (x < 0 || x > bitmap.getWidth()) {
            return -1;
        }
        if (y < 0 || y > bitmap.getHeight()) {
            return -1;
        }
        return bitmap.getPixel(x, y);
    }

    public static boolean bitmap2File(Bitmap bitmap, int quality, File output) {
        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(output));
            bitmap.compress(Bitmap.CompressFormat.PNG, quality, bos);
            bos.flush();
            bos.close();
            if (output.exists()){
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
