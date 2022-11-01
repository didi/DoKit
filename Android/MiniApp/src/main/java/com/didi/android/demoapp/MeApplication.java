package com.didi.android.demoapp;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;

import com.didichuxing.doraemonkit.DoKit;
import com.didichuxing.doraemonkit.DoKitCallBack;
import com.didichuxing.doraemonkit.kit.network.bean.NetworkRecord;

/**
 * didi Create on 2022/6/23 .
 * <p>
 * Copyright (c) 2022/6/23 by didiglobal.com.
 *
 * @author <a href="realonlyone@126.com">zhangjun</a>
 * @version 1.0
 * @Date 2022/6/23 5:21 下午
 * @Description 用一句话说明文件功能
 */
public class MeApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        DoKit.Builder builder = new DoKit.Builder(this);

        builder.productId("749a0600b5e48dd77cf8ee680be7b1b7")
            .disableUpload()
            .fileManagerHttpPort(8002)
            .mcWSPort(5555)
            .callBack(new DoKitCallBack() {
                @Override
                public void onCpuCallBack(float value, @NonNull String filePath) {
                    Log.e("onCpuCallBack()", "" + filePath);
                }

                @Override
                public void onFpsCallBack(float value, @NonNull String filePath) {
                    Log.e("onFpsCallBack()", "" + filePath);
                }

                @Override
                public void onMemoryCallBack(float value, @NonNull String filePath) {
                    Log.e("onMemoryCallBack()", "" + filePath);
                }

                @Override
                public void onNetworkCallBack(@NonNull NetworkRecord record) {
                    Log.e("onNetworkCallBack()", "" + record);
                }
            });
        builder.alwaysShowMainIcon(true);
        builder.build();

    }
}
