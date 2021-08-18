package com.didichuxing.doraemondemo;

import android.app.Application;
import android.content.Context;

import androidx.multidex.MultiDex;

import com.didichuxing.doraemondemo.dokit.DemoKit;
import com.didichuxing.doraemonkit.DoKit;
import com.didichuxing.doraemonkit.DoKitCallBack;
import com.didichuxing.doraemonkit.kit.AbstractKit;
import com.didichuxing.doraemonkit.kit.network.bean.NetworkRecord;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipelineConfig;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020/4/22-17:03
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class AopApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        List<AbstractKit> kits = new ArrayList<>();
        kits.add(new DemoKit());
        //测试环境:a49842eeebeb1989b3f9565eb12c276b
        //线上环境:749a0600b5e48dd77cf8ee680be7b1b7
        //new AopTest().test();
        ImagePipelineConfig config = ImagePipelineConfig.newBuilder(this)
                .setDiskCacheEnabled(false)
                .build();
        Fresco.initialize(this, config);

        //是否显示入口icon
        // DoraemonKit.setAwaysShowMainIcon(false);

        new DoKit.Builder(this)
                .productId("749a0600b5e48dd77cf8ee680be7b1b7")
                .disableUpload()
                .fileManagerHttpPort(9001)
                .mcWSPort(5555)
                .alwaysShowMainIcon(true)
                .callBack(new DoKitCallBack() {
                    @Override
                    public void onNetworkCallBack(@NotNull NetworkRecord record) {

                    }

                    @Override
                    public void onCpuCallBack(float value, @NotNull String filePath) {

                    }

                    @Override
                    public void onFpsCallBack(float value, @NotNull String filePath) {

                    }

                    @Override
                    public void onMemoryCallBack(float value, @NotNull String filePath) {

                    }


                })
                .build();

        //DoraemonKit.install(this, kits, "70e78c27f9174d68668d8a66a2b66483")
    }


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(base);
    }
}
