package com.didichuxing.doraemondemo;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;

import com.didichuxing.doraemonkit.DoraemonKit;
import com.didichuxing.doraemonkit.kit.AbstractKit;
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
        //测试环境:a49842eeebeb1989b3f9565eb12c276b
        //线上环境:749a0600b5e48dd77cf8ee680be7b1b7
        DoraemonKit.disableUpload();
        //是否显示入口icon
        //DoraemonKit.setAwaysShowMainIcon(false);
        DoraemonKit.install(this, null, "749a0600b5e48dd77cf8ee680be7b1b7");

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