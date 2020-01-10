package com.didichuxing.doraemondemo;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.StrictMode;
import android.support.multidex.MultiDex;

import com.didichuxing.doraemondemo.dokit.DemoKit;
import com.didichuxing.doraemonkit.DoraemonKit;
import com.didichuxing.doraemonkit.kit.IKit;
import com.didichuxing.doraemonkit.kit.webdoor.WebDoorManager;
import com.facebook.drawee.backends.pipeline.Fresco;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangweida on 2018/6/22.
 */

public class App extends Application {
    private static final String TAG = "App";
    public static Activity leakActivity;

    @Override
    public void onCreate() {
        super.onCreate();
        List<IKit> kits = new ArrayList<>();
        kits.add(new DemoKit());
        DoraemonKit.install(this, kits,"testProductId");
        //是否显示入口icon
        //DoraemonKit.setAwaysShowMianIcon(false);
        DoraemonKit.disableUpload();
        Fresco.initialize(this);
        DoraemonKit.setWebDoorCallback(new WebDoorManager.WebDoorCallback() {
            @Override
            public void overrideUrlLoading(Context context, String url) {
                Intent intent = new Intent(App.this, WebViewActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(WebViewActivity.KEY_URL, url);
                startActivity(intent);
            }
        });
        //严格检查模式
        //StrictMode.enableDefaults();

    }


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}