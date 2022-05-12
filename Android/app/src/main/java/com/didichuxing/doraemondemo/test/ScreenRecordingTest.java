package com.didichuxing.doraemondemo.test;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.projection.MediaProjectionManager;

import androidx.annotation.Nullable;

import com.didichuxing.doraemondemo.test.screen.ScreenRecordingDoKitView;
import com.didichuxing.doraemonkit.DoKit;

/**
 * didi Create on 2022/4/25 .
 * <p>
 * Copyright (c) 2022/4/25 by didiglobal.com.
 *
 * @author <a href="realonlyone@126.com">zhangjun</a>
 * @version 1.0
 * @Date 2022/4/25 11:24 上午
 * @Description 屏幕录制服务测试支持
 */

public class ScreenRecordingTest {
    public static final int REQUEST_MEDIA_PROJECTION = 100;

    private MediaProjectionManager mMediaProjectionManager;
    private Activity activity;

    public void start(Activity activity) {
        this.activity = activity;
        mMediaProjectionManager = (MediaProjectionManager) activity.getSystemService(Context.MEDIA_PROJECTION_SERVICE);

        activity.startActivityForResult(mMediaProjectionManager.createScreenCaptureIntent(), REQUEST_MEDIA_PROJECTION);
    }

    public void startService() {
        Intent nfIntent = new Intent(activity.getApplication(), ScreenRecordingService.class);
        activity.startService(nfIntent);
    }

    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        ScreenRecordingService.activity = activity;
        ScreenRecordingService.mMediaProjectionManager = mMediaProjectionManager;

        ScreenRecordingService.mResultCode = resultCode;
        ScreenRecordingService.mResultData = data;

        startService();
        DoKit.launchFloating(ScreenRecordingDoKitView.class);
    }
}
