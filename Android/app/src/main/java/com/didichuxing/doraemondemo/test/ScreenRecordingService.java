package com.didichuxing.doraemondemo.test;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.hardware.HardwareBuffer;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.IBinder;
import android.util.DisplayMetrics;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.didichuxing.doraemondemo.R;
import com.didichuxing.doraemondemo.mc.MCActivity;
import com.didichuxing.doraemondemo.test.screen.ScreenRecordingDoKitView;
import com.didichuxing.doraemonkit.DoKit;
import com.didichuxing.doraemonkit.kit.test.report.ScreenShotManager;
import com.didichuxing.doraemonkit.util.DoKitExecutorUtil;

import java.nio.ByteBuffer;

/**
 * didi Create on 2022/4/25 .
 * <p>
 * Copyright (c) 2022/4/25 by didiglobal.com.
 *
 * @author <a href="realonlyone@126.com">zhangjun</a>
 * @version 1.0
 * @Date 2022/4/25 11:47 上午
 * @Description 屏幕录制服务
 */

public class ScreenRecordingService extends Service {


    public static MediaProjectionManager mMediaProjectionManager;
    public static Activity activity;
    public static MediaProjection mMediaProjection;

    public static int mResultCode;
    public static Intent mResultData;

    private static ScreenRecordingService service;


    private ImageReader mImageReader;
    private ScreenShotManager screenShotManager = new ScreenShotManager("test/sc/");
    private boolean enable = true;

    public static void stopService() {
        if (service != null) {
            service.stopSelf();
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (service != null) {
            service.stopSelf();
        }
        service = this;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        service = null;
        enable = false;
        DoKit.removeFloating(ScreenRecordingDoKitView.class);
    }

    @SuppressLint("WrongConstant")
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        NotificationChannel channel = new NotificationChannel("NOTIFICATION_CHANNEL_ID", "NOTIFICATION_CHANNEL_NAME", NotificationManager.IMPORTANCE_MIN);
        channel.setDescription("NOTIFICATION_CHANNEL_DESC");

        if (activity == null || activity.getApplication() == null) {
            return START_STICKY;
        }

        NotificationManager mNM = (NotificationManager) getApplication().getSystemService(Context.NOTIFICATION_SERVICE);

        if (mNM != null) {
            mNM.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(activity, channel.getId());
        // 在API11之后构建Notification的方式
        Intent nfIntent = new Intent(activity, MCActivity.class);

        builder.setContentIntent(PendingIntent.getActivity(activity, 0, nfIntent, 0))
            .setLargeIcon(BitmapFactory.decodeResource(activity.getResources(), R.mipmap.ic_launcher))
            .setContentTitle("下拉列表中的Title")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentText("要显示的内容")
            .setWhen(System.currentTimeMillis());

        Notification notification = builder.build(); // 获取构建好的Notification
        notification.defaults = Notification.DEFAULT_SOUND; //设置为默认的声音

        startForeground(100, notification);


        mMediaProjection = mMediaProjectionManager.getMediaProjection(mResultCode, mResultData);

        DisplayMetrics metrics = activity.getResources().getDisplayMetrics();
        int windowWidth = metrics.widthPixels;
        int windowHeight = metrics.heightPixels;
        float mScreenDensity = metrics.density;

        ImageReader mImageReader = null; //ImageFormat.RGB_565
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            mImageReader = ImageReader.newInstance(windowWidth, windowHeight, PixelFormat.RGBA_8888, 2, HardwareBuffer.USAGE_CPU_WRITE_OFTEN);
        } else {
            mImageReader = ImageReader.newInstance(windowWidth, windowHeight, PixelFormat.RGBA_8888, 2);
        }
        VirtualDisplay mVirtualDisplay = mMediaProjection.createVirtualDisplay("screen-mirror",
            windowWidth, windowHeight, (int) mScreenDensity, DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
            mImageReader.getSurface(), null, null);

        this.mImageReader = mImageReader;

        DoKitExecutorUtil.execute(mRunnable);

        return super.onStartCommand(intent, flags, startId);
    }

    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            while (enable) {
                try {
                    Thread.sleep(100);
                    acquireLatestImage();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    };


    public void acquireLatestImage() {
        Image image = mImageReader.acquireLatestImage();
        int width = image.getWidth();
        int height = image.getHeight();
        final Image.Plane[] planes = image.getPlanes();
        if (planes.length > 0) {
            final ByteBuffer buffer = planes[0].getBuffer();
            int pixelStride = planes[0].getPixelStride();
            int rowStride = planes[0].getRowStride();
            int rowPadding = rowStride - pixelStride * width;
            Bitmap bitmap = Bitmap.createBitmap(width + rowPadding / pixelStride, height, Bitmap.Config.ARGB_8888);
            bitmap.copyPixelsFromBuffer(buffer);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height);

            ScreenRecordingDoKitView.Companion.updateScreen(bitmap);
//            screenShotManager.saveBitmap(bitmap, screenShotManager.createNextFileName());
        }

        image.close();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
