package com.didichuxing.doraemonkit.kit.colorpick;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.hardware.display.DisplayManager;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;

import com.didichuxing.doraemonkit.DoKitEnv;
import com.didichuxing.doraemonkit.util.AppUtils;
import com.didichuxing.doraemonkit.util.LogHelper;
import com.didichuxing.doraemonkit.util.UIUtils;

import java.nio.ByteBuffer;

/**
 * @author wanglikun
 * @date 2018/12/3
 */
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class ImageCapture {
    private static final String TAG = "ImageCapture";
    private MediaProjectionManager mMediaProjectionManager;
    private MediaProjection mMediaProjection;
    private ImageReader mImageReader;
    private boolean isCapturing;
    private Bitmap mBitmap;
    private ColorPickerDoKitView mColorPickerDokitView;


    public void init(Context context, Bundle bundle, ColorPickerDoKitView colorPickerDokitView) throws Exception {
        this.mColorPickerDokitView = colorPickerDokitView;
        PackageManager packageManager = DoKitEnv.requireApp().getPackageManager();
        ApplicationInfo applicationInfo = packageManager.getApplicationInfo(AppUtils.getAppPackageName(), 0);
        //适配Android Q
        if (applicationInfo.targetSdkVersion >= 29) {
            if (ColorPickManager.getInstance().getMediaProjection() != null) {
                colorPickerDokitView.onScreenServiceReady();
            } else {
                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && (bundle.getParcelable("data") instanceof Intent)) {
                        Intent dataIntent = bundle.getParcelable("data");
                        Intent intent = new Intent(context, ScreenRecorderService.class);
                        intent.putExtra("data", dataIntent);
                        context.startForegroundService(intent);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            mMediaProjectionManager = (MediaProjectionManager) context.getSystemService(Context.MEDIA_PROJECTION_SERVICE);
            if (mMediaProjectionManager != null) {
                mMediaProjection = mMediaProjectionManager.getMediaProjection(Activity.RESULT_OK, (Intent) bundle.getParcelable("data"));
                initImageRead(mMediaProjection);
            }
        }
    }

    /**
     *
     */
    @SuppressLint("WrongConstant")
    void initImageRead(MediaProjection mediaProjection) {
        if (mediaProjection == null) {
            LogHelper.e(TAG, "mediaProjection == null");
            return;
        }
        int width = UIUtils.getWidthPixels();
        int height = UIUtils.getRealHeightPixels();
        int dpi = UIUtils.getDensityDpi();
        //wiki:https://www.jianshu.com/p/d7eb518195fd
        mImageReader = ImageReader.newInstance(width, height, PixelFormat.RGBA_8888, 2);
        /**
         * 获取getSurface
         */
        mediaProjection.createVirtualDisplay("ScreenCapture",
                width, height, dpi,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                mImageReader.getSurface(), null, null);
    }

    /**
     * 截取最后一帧的bitmap
     */
    void capture() {
        if (isCapturing) {
            return;
        }
        if (mImageReader == null) {
            return;
        }
        isCapturing = true;
        //获取image对象
        Image image = mImageReader.acquireLatestImage();
        if (image == null) {
            return;
        }
        int width = image.getWidth();
        int height = image.getHeight();
        Image.Plane[] planes = image.getPlanes();
        ByteBuffer buffer = planes[0].getBuffer();
        int pixelStride = planes[0].getPixelStride();
        int rowStride = planes[0].getRowStride();
        int rowPaddingStride = rowStride - pixelStride * width;
        int rowPadding = rowPaddingStride / pixelStride;
        Bitmap recordBitmap = Bitmap.createBitmap(width + rowPadding, height, Bitmap.Config.ARGB_8888);
        recordBitmap.copyPixelsFromBuffer(buffer);
        mBitmap = Bitmap.createBitmap(recordBitmap, 0, 0, width, height);
        image.close();
        isCapturing = false;
    }

    Bitmap getPartBitmap(int x, int y, int width, int height) {
        if (mBitmap == null) {
            return null;
        }
        if (x < 0) {
            x = 0;
        }
        if (x + width > mBitmap.getWidth()) {
            x = mBitmap.getWidth() - width;
        }
        if (y < 0) {
            y = 0;
        }
        if (y + height > mBitmap.getHeight()) {
            y = mBitmap.getHeight() - height;
        }
        return Bitmap.createBitmap(mBitmap, x, y, width, height);
    }

    void destroy() {
        if (mImageReader != null) {
            mImageReader.close();
            mImageReader = null;
        }
        if (mMediaProjection != null) {
            mMediaProjection.stop();
            mMediaProjection = null;
        }
        mMediaProjectionManager = null;

        if (mBitmap != null) {
            mBitmap.recycle();
            mBitmap = null;
        }
    }
}
