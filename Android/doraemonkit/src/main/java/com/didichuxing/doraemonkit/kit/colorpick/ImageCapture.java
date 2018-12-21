package com.didichuxing.doraemonkit.kit.colorpick;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.hardware.display.DisplayManager;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;

import com.didichuxing.doraemonkit.util.UIUtils;

import java.nio.ByteBuffer;

/**
 * Created by wanglikun on 2018/12/3.
 */

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class ImageCapture {
    private MediaProjectionManager mMediaProjectionManager;
    private MediaProjection mMediaProjection;
    private ImageReader mImageReader;
    private boolean isCapturing;
    private Bitmap mBitmap;

    public ImageCapture() {

    }

    public void init(Context context, Bundle bundle) {
        mMediaProjectionManager = (MediaProjectionManager) context.getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        if (mMediaProjectionManager != null) {
            Intent intent = new Intent();
            intent.putExtras(bundle);
            mMediaProjection = mMediaProjectionManager.getMediaProjection(Activity.RESULT_OK, intent);
        }
        int width = UIUtils.getWidthPixels(context);
        int height = UIUtils.getRealHeightPixels(context);
        mImageReader = ImageReader.newInstance(width, height, PixelFormat.RGBA_8888, 2);
        mMediaProjection.createVirtualDisplay("ScreenCapture",
                width, height, UIUtils.getDensityDpi(context),
                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                mImageReader.getSurface(), null, null);
    }

    public void capture() {
        if (isCapturing) {
            return;
        }
        isCapturing = true;
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
        int rowPadding = rowStride - pixelStride * width;
        mBitmap = Bitmap.createBitmap(width + rowPadding / pixelStride, height, Bitmap.Config.ARGB_8888);
        mBitmap.copyPixelsFromBuffer(buffer);
        image.close();
        isCapturing = false;
    }

    public Bitmap getBitmap() {
        return mBitmap;
    }

    public void destroy() {
        mImageReader.close();
        mMediaProjection.stop();
        mMediaProjectionManager = null;
        mMediaProjection = null;
        mImageReader = null;
        if (mBitmap != null) {
            mBitmap.recycle();
            mBitmap = null;
        }
    }
}